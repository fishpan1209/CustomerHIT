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
public class NationalParkTest_parameter {
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

  
        long start;  
        ArrayList<Double> GA_optimal = new ArrayList();
        ArrayList<Double> GA_runtime = new ArrayList();
       
        
        NationalParkEvaluationFunction ef = new NationalParkRouteEvaluationFunction(parks);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new NationalParkCrossOver(ef);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
     
 
        for(int populationSize=150;populationSize<550;populationSize+=50){
        for (int i = 4; i < 16; i++) {
			int iter = (int) Math.pow(2, i);
			
			for (int j = 1; j < 11; j++) {
				System.out.println("\n" + "populationSize: "+populationSize+"  Iteration: "+iter+"  Round: "+j);
        start = System.currentTimeMillis();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(populationSize, 150, 20, gap);
        FixedIterationTrainer fit = new FixedIterationTrainer(ga, iter);
        fit.train();
        double optimal = 1/ef.value(ga.getOptimal())*0.0006214;//mile
        double runtime = System.currentTimeMillis() - start;
		System.out.println("Optimal: "
				+ optimal +"\nRuntime: "
				+ runtime + "ms");
		GA_optimal.add(optimal);
		GA_runtime.add(runtime);

	}
        }
        }

     		// write GA result to txt
     		try {
     			PrintWriter results = new PrintWriter("NationalPark_GA_populationSize.txt");
     			results.println("populationSize" + "," + "Iteration" + "," + "Round" + ","
     					+ "Optimal" + "," + "Runtime");
     			int k=0;
     			for(int populationSize=150;populationSize<550;populationSize+=50){
     				for (int i = 4; i < 16; i++) {
     					int iter = (int) Math.pow(2, i);

     					for (int j = 1; j < 11; j++) {
     						
     						results.println(populationSize+","+iter + "," + j + ","
     								+ GA_optimal.get(k) + "," + GA_runtime.get(k));
     						k++;

     					}
     				}
     			}
     			

     			results.close();
     		} catch (IOException ex) {
     			System.out.println("Unable to create results file:");
     			System.out.println(ex);
     		}
	

     	}
     }
