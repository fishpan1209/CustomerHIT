package opt.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

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
public class FlipFlopTest_FixedIteration {
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
        
        ArrayList<Double> RHC_optimal= new ArrayList();
		ArrayList<Double> RHC_runtime = new ArrayList();
		ArrayList<Double> SA_optimal= new ArrayList();
		ArrayList<Double> SA_runtime = new ArrayList();
		ArrayList<Double> GA_optimal= new ArrayList();
		ArrayList<Double> GA_runtime = new ArrayList();
		ArrayList<Double> MIMIC_optimal= new ArrayList();
		ArrayList<Double> MIMIC_runtime = new ArrayList();
		long start=0;
		
		for(int i=2;i<15;i++){
			int iter=(int) Math.pow(2,i);
			for(int j=0;j<10;j++){
		    System.out.println("\nIteration: "+iter+"  Round: "+j);
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, iter);
        start=System.currentTimeMillis();
        fit.train();
        double runtime=System.currentTimeMillis()-start;
        double optimal = ef.value(rhc.getOptimal());
        RHC_optimal.add(optimal);
        RHC_runtime.add(runtime);
        System.out.println("RHC: "+"Iteration: "+iter+"  Round: "+j+"  Optimal: "+optimal+"  runtime:"+runtime);
        
        SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
        fit = new FixedIterationTrainer(sa, iter);
        start=System.currentTimeMillis();
        fit.train();
        runtime=System.currentTimeMillis()-start;
        optimal = ef.value(sa.getOptimal());
        SA_optimal.add(optimal);
        SA_runtime.add(runtime);
        System.out.println("SA: "+"Iteration: "+iter+"  Round: "+j+"  Optimal: "+optimal+"  runtime:"+runtime);
        
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 20, gap);
        fit = new FixedIterationTrainer(ga, iter);
        start=System.currentTimeMillis();
        fit.train();
        runtime=System.currentTimeMillis()-start;
        optimal = ef.value(rhc.getOptimal());
        GA_optimal.add(optimal);
        GA_runtime.add(runtime);
        System.out.println("GA: "+"Iteration: "+iter+"  Round: "+j+"  Optimal: "+optimal+"  runtime:"+runtime);
        
        MIMIC mimic = new MIMIC(200, 5, pop);
        fit = new FixedIterationTrainer(mimic, iter);
        start=System.currentTimeMillis();
        fit.train();
        runtime=System.currentTimeMillis()-start;
        optimal = ef.value(mimic.getOptimal());
        MIMIC_optimal.add(optimal);
        MIMIC_runtime.add(runtime);
        System.out.println("MIMIC: "+"Iteration: "+iter+"  Round: "+j+"  Optimal: "+optimal+"  runtime:"+runtime);
    }
		}
		//write result
		try {
			PrintWriter RHC = new PrintWriter("FlipFlop_FixedIteration_RHC.txt");
			PrintWriter SA = new PrintWriter("FlipFlop_FixedIteration_SA.txt");
			PrintWriter GA = new PrintWriter("FlipFlop_FixedIteration_GA.txt");
			PrintWriter MIMIC = new PrintWriter("FlipFlop_FixedIteration_MIMIC.txt");
			RHC.println("Iteration" + "," + "Round" + ","
					+ "Optimal" + "," + "Runtime");
			SA.println("Iteration" + "," + "Round" + ","
					+ "Optimal" + "," + "Runtime");
			GA.println("Iteration" + "," + "Round" + ","
					+ "Optimal" + "," + "Runtime");
			MIMIC.println("Iteration" + "," + "Round" + ","
					+ "Optimal" + "," + "Runtime");
			int k=0;
				for (int i = 2; i < 15; i++) {
					int iter = (int) Math.pow(2, i);

					for (int j = 0; j < 10; j++) {
						
						RHC.println(iter + "," + j + ","
								+ RHC_optimal.get(k) + "," + RHC_runtime.get(k));
						SA.println(iter + "," + j + ","
								+ SA_optimal.get(k) + "," + SA_runtime.get(k));
						GA.println(iter + "," + j + ","
								+ GA_optimal.get(k) + "," + GA_runtime.get(k));
						MIMIC.println(iter + "," + j + ","
								+ MIMIC_optimal.get(k) + "," + MIMIC_runtime.get(k));
						k++;

					}
				}
			

			RHC.close();
			SA.close();
			GA.close();
			MIMIC.close();
		} catch (IOException ex) {
			System.out.println("Unable to create results file:");
			System.out.println(ex);
		}
    }
}
