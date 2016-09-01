package opt.test;

import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.text.*;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network on fixed iterations, and output learning curve data
 *
 * @author Hannah Lau
 * @version 1.0
 * @author Ting Pan
 * @version 2.0
 */
public class BankNN_LearningCurve {
    
    private static Instance[] Original = initializeInstancesTrain();
    private static Instance[] instances=Arrays.copyOfRange(Original, 0, (int)(2260*0.1));
    
    
    private static Instance[] testInstances = initializeInstancesTest();

    private static int inputLayer = 8, hiddenLayer = 5, outputLayer = 1;
    private static int trainingIterations = 2000;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    
    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set = new DataSet(instances);

    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
    private static String[] oaNames = {"RHC", "SA", "GA"};
    private static String results = "";

    private static DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) {
    	System.out.println("sample size: "+instances.length);
    	
        for(int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer});
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
        }
        
        oa[0] = new RandomizedHillClimbing(nnop[0]);
        oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1]);
        oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2]);

        
        ArrayList<Double> TrainAccuracy = new ArrayList();
        ArrayList<Double> TrainRecall = new ArrayList();
        ArrayList<Double> TestAccuracy = new ArrayList();
        ArrayList<Double> TestRecall = new ArrayList();
        ArrayList<Integer> trainedIter = new ArrayList();
        
        for(int i = 0; i < oa.length; i++) {
        	//optimize 5 times for each algorithm
        	for(int round =0; round<5; round++){
            double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0,TT=0,TF=0,FF=0,FT=0;
            train(oa[i], networks[i], oaNames[i]); //trainer.train();
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10,9);

            Instance optimalInstance = oa[i].getOptimal();
            networks[i].setWeights(optimalInstance.getData());
            
            //train
            double predicted, actual;
            start = System.nanoTime();
            
            for(int j = 0; j < instances.length; j++) {
                networks[i].setInputValues(instances[j].getData());
                networks[i].run();

                predicted = Double.parseDouble(instances[j].getLabel().toString());
                actual = Double.parseDouble(networks[i].getOutputValues().toString());
   
                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;
                if (predicted==0 ) {
                	if (Math.abs(predicted-actual)<0.5) TT++;
                	else TF++;
                }
                else {
                	if (Math.abs(predicted-actual)<0.5) FF++;
                	else FT++;
                }
                
               
            }
            double trainingAccuracy = correct/(correct+incorrect);
            double trainingRecall = FF/(FT+FF);
            
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);
            

            results +=  "\nResults for " + oaNames[i] + "\nTraining iteration = "+trainingIterations+"\nTraning round = "+round+": \nCorrectly classified " + correct + " instances." +
                        "\nIncorrectly classified " + incorrect + "\nTraining accuracy: "
                        + trainingAccuracy + "\nTraining time: " + df.format(trainingTime)
                        + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n"
                        +"TT="+TT+"  TF="+TF+"  classified as no\n"
                        +"FT="+FT+"  FF="+FF+"  classified as yes\n"
                        +"Training recall: "+trainingRecall+"\n";
           TrainAccuracy.add(trainingAccuracy);
           TrainRecall.add(trainingRecall);
        
        //test
        correct = 0;
        incorrect = 0;
        TT=0;
        TF=0;
        FT=0;
        FF=0;
       
        start = System.nanoTime();
        for(int j = 0; j < testInstances.length; j++) {
            networks[i].setInputValues(testInstances[j].getData());
            networks[i].run();

            predicted = Double.parseDouble(testInstances[j].getLabel().toString());
            actual = Double.parseDouble(networks[i].getOutputValues().toString());
            
            double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;
            if (predicted==0 ) {
            	if (Math.abs(predicted-actual)<0.5) TT++;
            	else TF++;
            }
            else {
            	if (Math.abs(predicted-actual)<0.5) FF++;
            	else FT++;
            }
        }
        double testingAccuracy = correct/(correct+incorrect);
        double testingRecall=FF/(FT+FF);
        end = System.nanoTime();
        testingTime += end - start;
        testingTime /= 2.0;
        testingTime /= Math.pow(10, 9);

        //results += df.format(incorrect / (correct + incorrect)) + ","
                 //+ df.format(trainingTime) + "," + df.format(testingTime) + "\n";
        results += "\nTesting..."+": \nCorrectly classified " + correct + " instances." +
                "\nIncorrectly classified " + incorrect + " instances.\nTesting accuracy: "
                + testingAccuracy
                + "\nTesting time: " + df.format(testingTime) + " seconds\n"
                +"TT="+TT+"  TF="+TF+"  classified as no\n"
                +"FT="+FT+"  FF="+FF+"  classified as yes\n"
                +"Testing recall: "+testingRecall+"\n";
        TestAccuracy.add(testingAccuracy);
        TestRecall.add(testingRecall);
 
        }
        }

        System.out.println(results);
   
    }

    private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
        System.out.println("\nTraining " + oaName + "\n---------------------------");
        
        for(int i = 0; i < trainingIterations; i++) {
        	
            oa.train();

            double error = 0;
            for(int j = 0; j < instances.length; j++) {
                network.setInputValues(instances[j].getData());
                network.run();

                Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
                example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                error += measure.value(output, example);
            }

            //System.out.println(df.format(error));
        }
        
    }

    private static Instance[] initializeInstancesTrain() {
       
        double[][][] attributes = new double[(int) (2260)][][];

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("src/opt/test/bank_TrainFeature_binary_2260.csv")));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                attributes[i] = new double[2][];
                attributes[i][0] = new double[8]; // 8 attributes
                attributes[i][1] = new double[1];

                for(int j = 0; j < 8; j++)
                    attributes[i][0][j] = Double.parseDouble(scan.next());

                attributes[i][1][0] = Double.parseDouble(scan.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
       
               
        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            // classifications range from 0 to 30; split into 0 - 14 and 15 - 30
            instances[i].setLabel(new Instance(attributes[i][1][0] < 1 ? 0 : 1));
        
        }
        System.out.println(instances.length);
       
        //shuffle instances
        Random rnd = ThreadLocalRandom.current();
        for(int i=instances.length-1;i>0;i--){
        	int index = rnd.nextInt(i+1);
        	Instance a = instances[index];
        	instances[index]=instances[i];
        	instances[i]=a;
        	
        }
        
       
        return instances;
    }
    
    private static Instance[] initializeInstancesTest() {

        double[][][] attributes = new double[1130][][];

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("src/opt/test/bank_TestFeature_binary_1130.csv")));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                attributes[i] = new double[2][];
                attributes[i][0] = new double[8]; // 8 attributes
                attributes[i][1] = new double[1];

                for(int j = 0; j < 8; j++)
                    attributes[i][0][j] = Double.parseDouble(scan.next());

                attributes[i][1][0] = Double.parseDouble(scan.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            // classifications range from 0 to 30; split into 0 - 14 and 15 - 30
            instances[i].setLabel(new Instance(attributes[i][1][0] < 1 ? 0 : 1));
        }

        return instances;
    }
}


