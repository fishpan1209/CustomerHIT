package opt.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import opt.OptimizationAlgorithm;
import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * A test using the flip flop evaluation function
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FlipFlopTest {
    /** The n value */
    private static final int N = 100;
   
    public static void main(String[] args) {
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new FlipFlopEvaluationFunction();
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        long start;
        ArrayList<Double> RHC_optimal= new ArrayList();
		ArrayList<Integer> RHC_iteration = new ArrayList();
		ArrayList<Double> SA_optimal= new ArrayList();
		ArrayList<Integer> SA_iteration = new ArrayList();
		ArrayList<Double> GA_optimal= new ArrayList();
		ArrayList<Integer> GA_iteration = new ArrayList();
		ArrayList<Double> MIMIC_optimal= new ArrayList();
		ArrayList<Integer> MIMIC_iteration = new ArrayList();
        
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
		double optimal = ef.value(rhc.getOptimal());
		int iter=1;
		int repeatedOptimal=0;
		int MaxRepeatedPlateau=1000;
		int MaxIterations = (int) Math.pow(2, 25);
		
		while(optimal<N && repeatedOptimal<MaxRepeatedPlateau && iter<MaxIterations && runtime<MaxTime){
			iter++;
			fit.train();
			
			double currOptimal=ef.value(rhc.getOptimal());
			if(currOptimal==optimal){
				repeatedOptimal++;
			}
			else{
				repeatedOptimal=0;
				
			}
			optimal = currOptimal;
			runtime = System.currentTimeMillis()-start;
		}
		RHC_optimal.add(optimal);
		RHC_iteration.add(iter);
    
        System.out.println("\nRHC: optimal: "+optimal+"\nIteration: "+iter+"\nRunTime: "+runtime);
        
        SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
        
        fit = new FixedIterationTrainer(sa, 1);
        start=System.currentTimeMillis();
        fit.train();
        runtime=System.currentTimeMillis()-start;
		optimal = ef.value(sa.getOptimal());
		iter=1;
	    repeatedOptimal=0;
		
		while(optimal<N && repeatedOptimal<MaxRepeatedPlateau && iter<MaxIterations && runtime<MaxTime){
			iter++;
			fit.train();
			
			double currOptimal=ef.value(sa.getOptimal());
			if(currOptimal==optimal){
				repeatedOptimal++;
			}
			else{
				repeatedOptimal=0;
				
			}
			optimal = currOptimal;
			runtime = System.currentTimeMillis()-start;
		}
		SA_optimal.add(optimal);
		SA_iteration.add(iter);
		
        System.out.println("\nSA: Optimal: "+optimal+"\nIteration: "+iter+"\nRunTime: "+runtime);
        
        
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 20, gap);
        fit = new FixedIterationTrainer(ga, 1);
        start=System.currentTimeMillis();
        fit.train();
        runtime=System.currentTimeMillis()-start;
		optimal = ef.value(ga.getOptimal());
		iter=1;
	    repeatedOptimal=0;
		
		while(optimal<N && repeatedOptimal<MaxRepeatedPlateau && iter<MaxIterations && runtime<MaxTime){
			iter++;
			fit.train();
			
			double currOptimal=ef.value(ga.getOptimal());
			if(currOptimal==optimal){
				repeatedOptimal++;
			}
			else{
				repeatedOptimal=0;
				
			}
			optimal = currOptimal;
			runtime = System.currentTimeMillis()-start;
		}
		GA_optimal.add(optimal);
		GA_iteration.add(iter);
		
        System.out.println("\nGA: Optimal: "+optimal+"\nIteration: "+iter+"\nRunTime: "+runtime);
        
        
        
        MIMIC mimic = new MIMIC(200, 5, pop);
        fit = new FixedIterationTrainer(mimic, 1);
        start=System.currentTimeMillis();
        fit.train();
        runtime=System.currentTimeMillis()-start;
		optimal = ef.value(mimic.getOptimal());
		iter=1;
	    repeatedOptimal=0;
		
		while(optimal<N && repeatedOptimal<MaxRepeatedPlateau && iter<MaxIterations && runtime<MaxTime){
			iter++;
			fit.train();
			
			double currOptimal=ef.value(mimic.getOptimal());
			if(currOptimal==optimal){
				repeatedOptimal++;
			}
			else{
				repeatedOptimal=0;
				
			}
			optimal = currOptimal;
			runtime = System.currentTimeMillis()-start;
		}
		
		MIMIC_optimal.add(optimal);
		MIMIC_iteration.add(iter);
        System.out.println("\nMIMIC: Optimal: "+optimal+"\nIteration: "+iter+"\nRunTime: "+runtime);
        
        
        }
        }
     // write RHC result to txt
     		try {
     			PrintWriter results = new PrintWriter("FlipFlop_RHC.txt");
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
     			PrintWriter results = new PrintWriter("FlipFlop_SA.txt");
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
     			PrintWriter results = new PrintWriter("FlipFlop_GA.txt");
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
     			PrintWriter results = new PrintWriter("FlipFlop_MIMIC.txt");
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

