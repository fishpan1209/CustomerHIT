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
public class FlipFlopTest_parameter {
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
        
		ArrayList<Double> SA_optimal= new ArrayList();
		ArrayList<Double> SA_runtime = new ArrayList();
		
        /**
		//fixed cooling rate, change T
		for(int T=0;T<550;T+=50){
		double cooling = 0.95;
        **/
		//fixed T, change cooling
		for(double cooling = 0.95; cooling>0.1;cooling-=0.1){
			int T=100;
		for (int i=2;i<20;i++){
        int iter=(int)Math.pow(2, i);
        for(int j=0;j<10;j++){
        	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        	System.out.println("\nInitial cooling: "+cooling+"  Iteration: "+iter+"  Round: "+j);
      
        SimulatedAnnealing sa = new SimulatedAnnealing(T, cooling, hcp);
        
        FixedIterationTrainer fit = new FixedIterationTrainer(sa, iter);
        start=System.currentTimeMillis();
        fit.train();
        double runtime=System.currentTimeMillis()-start;
		double optimal = ef.value(sa.getOptimal());
		SA_optimal.add(optimal);
		SA_runtime.add(runtime);
		
        System.out.println("\nSA: Optimal: "+optimal+"\nIteration: "+iter+"\nRunTime: "+runtime);

        
        }
        }
		}
     
     		// write SAA result to txt
     		try {
     			PrintWriter results = new PrintWriter("FlipFlop_SA_T100_cooling.txt");
     			results.println("Initial cooling"+","+"Iteration" + "," + "Round" + ","
     					+ "Optimal" + "," + "Runtime");
     			int k=0;
     			for(double cooling=0.95;cooling>0.1;cooling-=0.1){
     			for (int i=2;i<20;i++){
     		        double iter=Math.pow(2, i);
     		        for(int j=0;j<10;j++){
    
     						results.println(cooling+","+iter + "," + (j+1) + "," + SA_optimal.get(k) + ","
     								 + SA_runtime.get(k));
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

