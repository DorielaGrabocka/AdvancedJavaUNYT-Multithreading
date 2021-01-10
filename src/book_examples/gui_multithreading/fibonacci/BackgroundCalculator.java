/*******************************************************************************
 * Copyright (c) 28/12/2020. Author Doriela Grabocka. All rights reserved.
 ******************************************************************************/

package book_examples.gui_multithreading.fibonacci;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class BackgroundCalculator extends SwingWorker<Long, Object> {
    private final int n;//Fibonacci number to calculate
    private final JLabel resultLabel;//JLabel to display result

    public BackgroundCalculator(int n, JLabel resultLabel) {
        this.n = n;
        this.resultLabel = resultLabel;
    }

    //code to be run in a worker thread
    public Long doInBackground(){
        return fibonaci(n);
    }

    protected void done(){
        try{
            resultLabel.setText(get().toString());
        }
        catch(InterruptedException e){
            resultLabel.setText("Interrupted while waiting for the results.");
        }
        catch(ExecutionException e){
            resultLabel.setText("Error encountered while performing calculation.");
        }
    }

    public long fibonaci(long n){
        if(n == 0 || n==1 ){
            return n;
        }
        else
            return fibonaci(n-1)+fibonaci(n-2);
    }
}
