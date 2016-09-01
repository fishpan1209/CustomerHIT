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
public class NationalParkTest_FixedTime {
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
        
        ArrayList<Double> RHC_optimal= new ArrayList();
		ArrayList<Integer> RHC_iteration = new ArrayList();
		ArrayList<Double> SA_optimal= new ArrayList();
		ArrayList<Integer> SA_iteration = new ArrayList();
		ArrayList<Double> GA_optimal= new ArrayList();
		ArrayList<Integer> GA_iteration = new ArrayList();
		ArrayList<Double> MIMIC_optimal= new ArrayList();
		ArrayList<Integer> MIMIC_iteration = new ArrayList();
        
        
	        
        NationalParkEvaluationFunction ef = new NationalParkRouteEvaluationFunction(parks);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new NationalParkCrossOver(ef);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        
     
        for (int i=5;i<16;i++){
            double MaxTime=Math.pow(2, i);
            for(int j=0;j<10;j++){
            	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            	System.out.println("\nMaxTime: "+MaxTime+"  Round: "+j);
            RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
            FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 1);
            start = System.currentTimeMillis();
            fit.train();
            double runtime = System.currentTimeMillis() - start;
            double optimal = 1/ef.value(rhc.getOptimal())*0.0006214;//mile
    		int iter=1;
    		int repeatedOptimal=0;
    		int MaxRepeatedPlateau=10;
    		int MaxIterations = (int) Math.pow(2, 15);
    	
    		
    		while(iter<MaxIterations && runtime<MaxTime){
    			iter++;
    			fit.train();
  
    			runtime = System.currentTimeMillis()-start;
    		}
    		optimal = 1/ef.value(rhc.getOptimal())*0.0006214;//mile
    		RHC_optimal.add(optimal);
    		RHC_iteration.add(iter);
        
            System.out.println("\nRHC: optimal: "+optimal+"\nIteration: "+iter+"\nRunTime: "+runtime);
            
            SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
            
            fit = new FixedIterationTrainer(sa, 1);
            start=System.currentTimeMillis();
            fit.train();
            runtime=System.currentTimeMillis()-start;
            optimal = 1/ef.value(sa.getOptimal())*0.0006214;//mile
    		iter=1;
    	    repeatedOptimal=0;
    		
    	    while(iter<MaxIterations && runtime<MaxTime){
    			iter++;
    			fit.train();
  
    			runtime = System.currentTimeMillis()-start;
    		}
    	    optimal = 1/ef.value(sa.getOptimal())*0.0006214;//mile
    		SA_optimal.add(optimal);
    		SA_iteration.add(iter);
    		
            System.out.println("\nSA: Optimal: "+optimal+"\nIteration: "+iter+"\nRunTime: "+runtime);
            
            
            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 20, gap);
            fit = new FixedIterationTrainer(ga, 1);
            start=System.currentTimeMillis();
            fit.train();
            runtime=System.currentTimeMillis()-start;
            optimal = 1/ef.value(ga.getOptimal())*0.0006214;//mile
    		iter=1;
    	    repeatedOptimal=0;
    		
    	    while(iter<MaxIterations && runtime<MaxTime){
    			iter++;
    			fit.train();
  
    			runtime = System.currentTimeMillis()-start;
    		}
    	    optimal = 1/ef.value(ga.getOptimal())*0.0006214;//mile
    		GA_optimal.add(optimal);
    		GA_iteration.add(iter);
    		
            System.out.println("\nGA: Optimal: "+optimal+"\nIteration: "+iter+"\nRunTime: "+runtime);
            
            
            // for mimic we use a sort encoding
            
            ef = new NationalParkSortEvaluationFunction(parks);
            int[] ranges = new int[N];
            Arrays.fill(ranges, N);
            odd = new  DiscreteUniformDistribution(ranges);
            Distribution df = new DiscreteDependencyTree(.1, ranges); 
            ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
            
            MIMIC mimic = new MIMIC(200, 5, pop);
            fit = new FixedIterationTrainer(mimic, 1);
            start=System.currentTimeMillis();
            fit.train();
            runtime=System.currentTimeMillis()-start;
            optimal = 1/ef.value(mimic.getOptimal())*0.0006214;//mile
    		iter=1;
    	    repeatedOptimal=0;
    		
    	    while(iter<MaxIterations && runtime<MaxTime){
    			iter++;
    			fit.train();
  
    			runtime = System.currentTimeMillis()-start;
    		}
    	    optimal = 1/ef.value(mimic.getOptimal())*0.0006214;//mile
    		MIMIC_optimal.add(optimal);
    		MIMIC_iteration.add(iter);
            System.out.println("\nMIMIC: Optimal: "+optimal+"\nIteration: "+iter+"\nRunTime: "+runtime);
            
            
            }
            }
         // write RHC result to txt
         		try {
         			PrintWriter results = new PrintWriter("NationalPark_FixedTime_RHC.txt");
         			results.println("MaxTime" + "," + "Round" + ","
         					+ "Optimal" + "," + "Iteration");
         			int k=0;
         			for (int i=5;i<16;i++){
         		        double MaxTime=Math.pow(2, i);
         		        for(int j=0;j<10;j++){
        
         						results.println(MaxTime + "," + (j+1) + "," + RHC_optimal.get(k) + ","
         								 + RHC_iteration.get(k));
         						k++;

         					}
         				}
       
         			results.close();
         		} catch (IOException ex) {
         			System.out.println("Unable to create results file:");
         			System.out.println(ex);
         		}
         		
         	
         		// write SAA result to txt
         		try {
         			PrintWriter results = new PrintWriter("NationalPark_FixedTime_SA.txt");
         			results.println("MaxTime" + "," + "Round" + ","
         					+ "Optimal" + "," + "Iteration");
         			int k=0;
         			for (int i=5;i<16;i++){
         		        double MaxTime=Math.pow(2, i);
         		        for(int j=0;j<10;j++){
        
         						results.println(MaxTime + "," + (j+1) + "," + SA_optimal.get(k) + ","
         								 + SA_iteration.get(k));
         						k++;

         					}
         				}
       
         			results.close();
         		} catch (IOException ex) {
         			System.out.println("Unable to create results file:");
         			System.out.println(ex);
         		}

         		// write GA result to txt
         		try {
         			PrintWriter results = new PrintWriter("NationalPark_FixedTime_GA.txt");
         			results.println("MaxTime" + "," + "Round" + ","
         					+ "Optimal" + "," + "Iteration");
         			int k=0;
         			for (int i=5;i<16;i++){
         		        double MaxTime=Math.pow(2, i);
         		        for(int j=0;j<10;j++){
        
         						results.println(MaxTime + "," + (j+1) + "," + GA_optimal.get(k) + ","
         								 + GA_iteration.get(k));
         						k++;

         					}
         				}
       

         			results.close();
         		} catch (IOException ex) {
         			System.out.println("Unable to create results file:");
         			System.out.println(ex);
         		}

         		// write MIMIC result to txt
         		try {
         			PrintWriter results = new PrintWriter("NationalPark_FixedTime_MIMIC.txt");
         			results.println("MaxTime" + "," + "Round" + ","
         					+ "Optimal" + "," + "Iteration");
         			int k=0;
         			for (int i=5;i<16;i++){
         		        double MaxTime=Math.pow(2, i);
         		        for(int j=0;j<10;j++){
        
         						results.println(MaxTime + "," + (j+1) + "," + MIMIC_optimal.get(k) + ","
         								 + MIMIC_iteration.get(k));
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

