/*******************************************************************************
 * Copyright (c) 28/12/2020. Author Doriela Grabocka. All rights reserved.
 ******************************************************************************/

package book_examples.gui_multithreading.sieve_of_eratostene;

import javax.swing.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class PrimeCalculator extends SwingWorker<Integer, Integer> {
    private static final SecureRandom generator = new SecureRandom();
    private final JTextArea intermediateJTextArea;
    private final JButton getPrimesJButton;
    private final JButton cancelJButton;
    private final JLabel statusJLabel;//displays status of calculation
    private final boolean[] primes;//boolean array for finding primes

    public PrimeCalculator(int max, JTextArea intermediateJTextArea, JButton getPrimesJButton,
                           JButton cancelJButton, JLabel statusJLabel) {
        this.intermediateJTextArea = intermediateJTextArea;
        this.getPrimesJButton = getPrimesJButton;
        this.cancelJButton = cancelJButton;
        this.statusJLabel = statusJLabel;
        this.primes = new boolean[max];

        Arrays.fill(primes, true);//initialize all primes to  true
    }

    public Integer doInBackground(){
        int count=0;//the number of primes found

        for(int i=2; i<primes.length;i++){
            if(isCancelled()){
                return count;
            }
            else{
                setProgress(100*(i+1)/ primes.length);

                try{
                    //sleep so that the event dispath memory queue will not run out of
                    //memory
                    Thread.sleep(generator.nextInt(5));
                }
                catch (InterruptedException e){
                    statusJLabel.setText("Worker thread was interrupted!");
                    return count;
                }

                if(primes[i]){
                    publish(i);
                    ++count;

                    for(int j=i+i; j< primes.length;j=j+i){
                        primes[j]=false;
                    }
                }
            }
        }
        return count;
    }

    protected void process(List<Integer> publishedVals){
        for(int i=0; i< publishedVals.size();i++){
            intermediateJTextArea.append(publishedVals.get(i)+"\n");
        }
    }

    protected void done(){
        getPrimesJButton.setEnabled(true);
        cancelJButton.setEnabled(false);
        try{
            statusJLabel.setText("Found "+ get()+" primes.");
        }
        catch (InterruptedException | ExecutionException | CancellationException e){
            statusJLabel.setText(e.getMessage());
        }
    }
}
