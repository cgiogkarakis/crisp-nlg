package crisp.evaluation;

import crisp.converter.BackoffModelProbCRISPConverter;
import crisp.converter.FastCRISPConverter;
import crisp.planningproblem.Problem;
import crisp.planningproblem.Domain;
import crisp.result.DerivationTreeBuilder;
import crisp.result.CrispDerivationTreeBuilder;
import crisp.result.PCrispDerivationTreeBuilder;

import de.saar.penguin.tag.grammar.Grammar;
import de.saar.penguin.tag.grammar.ProbabilisticGrammar;
import de.saar.penguin.tag.codec.PCrispXmlInputCodec;
import de.saar.penguin.tag.derivation.DerivationTree;
import de.saar.penguin.tag.derivation.DerivedTree;

import de.saar.chorus.term.Term;

import crisp.converter.ProbCRISPConverter;

import crisp.converter.TreeModelProbCRISPConverter;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;

import java.io.StringReader;
import java.io.StringWriter;    
import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import java.util.Properties;

/**
 * Provides basic functionality for large scale experiments with the generation system. 
 */
public class BatchExperiment {         

    private static final String GRAMMAR_NAME = "dummy";
    private static final String DATABASE_PROPERTIES_FILE = "database.properties";
    


    
    
    
    Grammar grammar; 
    DatabaseInterface database;
    String resultTable;
    Queue<Integer> batch;        
    
    Properties properties;

    public BatchExperiment(Grammar grammar, DatabaseInterface database, String resultTable) {


        this.grammar = grammar;
        this.database = database;
        this.resultTable = resultTable;
        this.batch = new LinkedList<Integer>();       
    }
    
    public void addToBatch(int sentenceID){
        batch.offer(sentenceID);
    }
    
    public void runExperiment(){
        while (! batch.isEmpty()) {                        
                processNextSentence();            
        }
    }
    
    public void processNextSentence(){
        processSentence(batch.poll());
    }
    
    
    private void processSentence(int sentenceID) {
        System.out.println("Processing sentence #"+sentenceID);
        
        DerivationTree derivTree = null;
        DerivedTree derivedTree = null;
        String yield = null;
        long preprocessingTime = 0;
        long searchTime = 0;
        long creationTime = 0;
        int domainSize = 0;
        

        BackoffModelProbCRISPConverter converter = new BackoffModelProbCRISPConverter();
        PlannerInterface planner = new LamaPlannerInterface();
        List<Term> plan = null;

        Domain domain = new Domain();
        Problem problem = new Problem();

        try{
            Set<Term> semantics = database.getSentenceSemantics("dbauer_PTB_semantics",sentenceID);
            String rootIndex = database.getRootIndex("dbauer_PTB_semantics",sentenceID);
        
            String xmlProblem = createXMLProblem(semantics, rootIndex, "problem-"+sentenceID, GRAMMAR_NAME);
            
            System.out.print("  converting...");
                            
            long start = System.currentTimeMillis();
            converter.convert(grammar, new StringReader(xmlProblem), domain, problem);
            domainSize = domain.getActions().size();



            creationTime = System.currentTimeMillis()-start;
            
            System.out.println("done in "+creationTime+"ms.");
            System.out.println("  Starting planner... ");
            plan = planner.runPlanner(domain, problem);
           
            // Build derivation and derived tree
            DerivationTreeBuilder derivationTreeBuilder = new PCrispDerivationTreeBuilder(grammar);
            derivTree = derivationTreeBuilder.buildDerivationTreeFromPlan(plan, domain);
            derivedTree = derivTree.computeDerivedTree(grammar);
            yield = derivedTree.yield();
            preprocessingTime = planner.getPreprocessingTime();
            searchTime = planner.getSearchTime();
            System.out.println("   Result is: "+yield);
            database.writeResults(resultTable, sentenceID, domainSize, derivTree, derivedTree, creationTime, preprocessingTime, searchTime, null);
        } catch (SQLException e) {            
            System.err.println("Couldn't process sentence #"+sentenceID);
            System.err.println("Error in SQL connection: "+e);
            return;
        } catch (Exception e) {         
            System.err.println("Couldn't process sentence #"+sentenceID);
            e.printStackTrace();
            // Write error tag to database
            try {
                database.writeResults(resultTable, sentenceID, domainSize, null, null, creationTime, 0, 0 , e.toString());
            }    catch (SQLException f) {            
                System.err.println("Couldn't write error message to database.");
                System.err.println("Error in SQL connection: "+f);                
            }         
        } finally {
           //converter = null;
           planner = null;
           plan = null;
           problem = null;
           domain = null;
         //  System.gc();
        }
    }
          
    
    private String createXMLProblem(Set<Term> semantics, String rootIndex, String name, String grammar) {
        StringWriter writer = new StringWriter();
        
        writer.write("<crispproblem name=\"");
        writer.write(name);
        writer.write("\" grammar=\"");
        writer.write(grammar);
        writer.write("\" cat=\"S\" index=\"");
        writer.write(rootIndex);
        writer.write("\" plansize=\"");
        writer.write(new Integer(semantics.size()).toString());
        writer.write("\">\n");

        // Everything in the KB becomes communicative goal
        for (Term t : semantics) {
            writer.write("<world>");
            writer.write(t.toString());
            writer.write("</world>\n");
            writer.write("<commgoal>");
            writer.write(t.toString());
            writer.write("</commgoal>\n");                        
        }
        
        writer.write("</crispproblem>\n");
        return writer.toString();        
    }
    
    
    public static void main(String[] args) throws Exception{
        
        System.out.print("Parsing grammar...");
        PCrispXmlInputCodec codec = new PCrispXmlInputCodec();
		ProbabilisticGrammar<Term> grammar = new ProbabilisticGrammar<Term>();	
        codec.parse(new File(args[0]), grammar);
        System.out.println("done");

        // Read properties file.
        Properties props = new Properties();

        try {
            props.load(new FileInputStream(DATABASE_PROPERTIES_FILE));
        } catch (IOException e) {
            System.err.print("Couldn't read properties file "+DATABASE_PROPERTIES_FILE);
        }

        String database = props.getProperty("databaseName");
        String username = props.getProperty("userName");
        String password = props.getProperty("password");
        String resulttable = props.getProperty("resultTable");

        System.out.println("Will connect to "+database+" as user "+username);

        System.out.print("Initializing experiment ...");
        BatchExperiment exp1 = new BatchExperiment(grammar,                                                   
                                                   new MySQLInterface(database, username, password),
                                                   resulttable);
                                                   
        int start = new Integer(args[1]);
        int end = new Integer(args[2]);
        
        int[] testset = {2, 23, 25, 53, 54, 64, 79, 102, 112, 145, 150, 155, 181, 193, 196, 211, 226, 247, 264, 293, 312, 333, 346, 350, 367, 369, 379, 386, 424, 431, 437, 5,
01, 527, 588, 589, 593, 612, 619, 626, 669, 680, 685, 700, 709, 789, 793, 799, 802, 811, 848, 865, 911, 914, 923, 925, 964, 971, 981, 1015, 1019, 10,
28, 1051, 1098, 1122, 1145, 1147, 1155, 1169, 1201, 1206, 1226, 1263, 1284, 1287, 1310, 1321, 1325, 1365, 1369, 1376, 1409, 1415, 1422, 1455, 1457,
1469, 1490, 1520, 1550, 1558, 1595, 1619, 1635, 1646, 1647, 1657, 1672, 1697, 1723, 1739};


        for (int i=0; i<=testset.length; i++) {
            exp1.addToBatch(testset[i]);
        }
        
        System.out.println("Done.");
        System.out.println("Running experiment...");
        exp1.runExperiment();
    }
    
}
