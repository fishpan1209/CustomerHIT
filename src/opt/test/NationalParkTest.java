package opt.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.SwapNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 * 
 * @author Ting Pan tpan35@gatech.edu
 * @version 2.0
 */
public class NationalParkTest {
    /** The n value */
    private static final int N = 55;
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) {
    	/**
        Random random = new Random();
        // create the random points
        double[][] points = new double[N][2];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = random.nextDouble();
            points[i][1] = random.nextDouble();   
        }
        **/
    	
        //read data
        double[][] parks = new double[N][2];

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("src/opt/test/NationalPark.csv")));

            for(int i = 0; i < parks.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                for(int j = 0; j < 2; j++){
                    parks[i][j] = Double.parseDouble(scan.next());
                    //System.out.println(parks[i][j]);
                }
              
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // for rhc, sa, and ga we use a permutation based encoding
        long start;
        
        ArrayList<Double> RHC_optimal = new ArrayList();
        ArrayList<Double> SAA_optimal = new ArrayList();
        ArrayList<Double> GA_optimal = new ArrayList();
        ArrayList<Double> MIMIC_optimal = new ArrayList();
        ArrayList<Double> RHC_runtime = new ArrayList();
        ArrayList<Double> SAA_runtime = new ArrayList();
        ArrayList<Double> GA_runtime = new ArrayList();
        ArrayList<Double> MIMIC_runtime = new ArrayList();
        
        
	        
        NationalParkEvaluationFunction ef = new NationalParkRouteEvaluationFunction(parks);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new NationalParkCrossOver(ef);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
     
        
        for (int i = 4; i < 25; i++) {
			int iter = (int) Math.pow(2, i);
			System.out.println("\n" + "RHC: ");
			for (int j = 1; j < 11; j++) {
        	
       //RHC
        start = System.currentTimeMillis();
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, iter);
        fit.train();
        double optimal = 1/ef.value(rhc.getOptimal())*0.0006214;//mile
        double runtime = System.currentTimeMillis() - start;
		System.out.println("Iteration="
				+ iter + "   Round=" + j + "   Optimal: "
				+ optimal + "   " + "Runtime: "
				+ runtime + "ms");
		RHC_optimal.add(optimal);
		RHC_runtime.add(runtime);

	}
}
        // write RHC result to txt
 		try {
 			PrintWriter results = new PrintWriter("NationalPark_RHC.txt");
 			results.println("Iteration" + "," + "Round" + ","
 					+ "Optimal" + "," + "Runtime");
 			int k=0;
 			
 				for (int i = 4; i < 25; i++) {
 					int iter = (int) Math.pow(2, i);

 					for (int j = 1; j < 11; j++) {
 						
 						results.println(iter + "," + j + ","
 								+ RHC_optimal.get(k) + "," + RHC_runtime.get(k));
 						
 						System.out.println(RHC_optimal.get(k) + "," + RHC_runtime.get(k));
 						k++;

 					}
 				}
 			

 			results.close();
 		} catch (IOException ex) {
 			System.out.println("Unable to create results file:");
 			System.out.println(ex);
 		}
 	
 		//SAA
        for (int i = 4; i < 25; i++) {
			int iter = (int) Math.pow(2, i);
			System.out.println("\n" + "SAA: ");
			for (int j = 1; j < 11; j++) {
	    start = System.currentTimeMillis();
        SimulatedAnnealing sa = new SimulatedAnnealing(1E12, .95, hcp);
        FixedIterationTrainer fit = new FixedIterationTrainer(sa, iter);
        fit.train();
        double optimal = 1/ef.value(sa.getOptimal())*0.0006214;//mile
        double runtime = System.currentTimeMillis() - start;
		System.out.println("Iteration="
				+ iter + "   Round=" + j + "   Optimal: "
				+ optimal + "   " + "Runtime: "
				+ runtime + "ms");
		SAA_optimal.add(optimal);
		SAA_runtime.add(runtime);

	}
}
     // write SAA result to txt
 		try {
 			PrintWriter results = new PrintWriter("NationalPark_SAA.txt");
 			results.println("N" + "," + "Iteration" + "," + "Round" + ","
 					+ "Optimal" + "," + "Runtime");
 			int k=0;
 			
 				for (int i = 4; i < 25; i++) {
 					int iter = (int) Math.pow(2, i);

 					for (int j = 1; j < 11; j++) {
 						
 						results.println(iter + "," + j + ","
 								+ SAA_optimal.get(k) + "," + SAA_runtime.get(k));
 						k++;

 					}
 				}
     			results.close();
     		} catch (IOException ex) {
     			System.out.println("Unable to create results file:");
     			System.out.println(ex);
     		}
 	
        
        for (int i = 4; i < 16; i++) {
			int iter = (int) Math.pow(2, i);
			System.out.println("\n" + "GA: ");
			for (int j = 1; j < 11; j++) {
        start = System.currentTimeMillis();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 20, gap);
        FixedIterationTrainer fit = new FixedIterationTrainer(ga, iter);
        fit.train();
        double optimal = 1/ef.value(ga.getOptimal())*0.0006214;//mile
        double runtime = System.currentTimeMillis() - start;
		System.out.println("Iteration="
				+ iter + "   Round=" + j + "   Optimal: "
				+ optimal + "   " + "Runtime: "
				+ runtime + "ms");
		GA_optimal.add(optimal);
		GA_runtime.add(runtime);

	}
        }

     		// write GA result to txt
     		try {
     			PrintWriter results = new PrintWriter("NationalPark_GA.txt");
     			results.println("N" + "," + "Iteration" + "," + "Round" + ","
     					+ "Optimal" + "," + "Runtime");
     			int k=0;
     			
     				for (int i = 4; i < 16; i++) {
     					int iter = (int) Math.pow(2, i);

     					for (int j = 1; j < 11; j++) {
     						
     						results.println(iter + "," + j + ","
     								+ GA_optimal.get(k) + "," + GA_runtime.get(k));
     						k++;

     					}
     				}
     			

     			results.close();
     		} catch (IOException ex) {
     			System.out.println("Unable to create results file:");
     			System.out.println(ex);
     		}


     
        // for mimic we use a sort encoding
        
        ef = new NationalParkSortEvaluationFunction(parks);
        int[] ranges = new int[N];
        Arrays.fill(ranges, N);
        odd = new  DiscreteUniformDistribution(ranges);
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        for (int i = 4; i < 16; i++) {
			int iter = (int) Math.pow(2, i);
			System.out.println("\n" + "MIMIC: ");
			for (int j = 1; j < 11; j++) {
        start = System.currentTimeMillis();
        MIMIC mimic = new MIMIC(200, 100, pop);
        FixedIterationTrainer fit = new FixedIterationTrainer(mimic, iter);
        fit.train();
        double optimal = 1/ef.value(mimic.getOptimal())*0.0006214;//mile
        double runtime = System.currentTimeMillis() - start;
		System.out.println("Iteration="
				+ iter + "   Round=" + j + "   Optimal: "
				+ optimal + "   " + "Runtime: "
				+ runtime + "ms");
		MIMIC_optimal.add(optimal);
		MIMIC_runtime.add(runtime);

	}
}

 		// write MIMIC result to txt
 		try {
 			PrintWriter results = new PrintWriter("NationalPark_MIMIC.txt");
 			results.println("N" + "," + "Iteration" + "," + "Round" + ","
 					+ "Optimal" + "," + "Runtime");
 			int k=0;
 			
 				for (int i = 4; i < 16; i++) {
 					int iter = (int) Math.pow(2, i);

 					for (int j = 1; j < 11; j++) {
 						
 						results.println(iter + "," + j + ","
 								+ MIMIC_optimal.get(k) + ","
 								+ MIMIC_runtime.get(k));
 						k++;

 					}
 				}
 			

 			results.close();
 		} catch (IOException ex) {
 			System.out.println("Unable to create results file:");
 			System.out.println(ex);
 		}

    			

     	}
     }
