/*******************************************************************************
 * Copyright (c) 28/12/2020. Author Doriela Grabocka. All rights reserved.
 ******************************************************************************/

package book_examples.gui_multithreading.fibonacci;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FibonacciNumbers extends JFrame {
    //all components needed for calculating the user entered Fibonacci
    private final JPanel workerJPanel = new JPanel(new GridLayout(2,2,5,5));
    private final JTextField numberJTextField = new JTextField();
    private final JButton goJButton = new JButton("Go");
    private final JLabel fibonacciJLabel = new JLabel();

    //components and variables for getting the next Fibonacci number
    private final JPanel eventThreadJPanel = new JPanel(new GridLayout(2,2,5,5));
    private long n1 = 0;//first fib number
    private long n2 = 1; //second fib number
    private int count = 1;//current fibonacci number to display
    private final JLabel nJLabel = new JLabel("Fibonacci of 1: ");
    private final JLabel nFibonacciJLabel = new JLabel(String.valueOf(n2));
    private final JButton nextNumberJButton = new JButton("Next Number");

    public FibonacciNumbers(){
        super("Fibonacci Numbers");
        setLayout(new GridLayout(2,2,10,10));

        workerJPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "With SwingWorker"));
        workerJPanel.add(new JLabel("Get Fibonacci of: "));
        workerJPanel.add(numberJTextField);
        goJButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                            int n;

                            try{
                                n = Integer.parseInt(numberJTextField.getText());
                            }catch (NumberFormatException ex){
                                fibonacciJLabel.setText("Enter an integer.");
                                return;
                            }
                            //indicate that the calculation has  began
                            fibonacciJLabel.setText("Calculating...");
                            BackgroundCalculator task = new BackgroundCalculator(n, fibonacciJLabel);
                            task.execute();//execute the task
                        }
                    }//end anonymous inner class
        );//end call to addActionListener
        workerJPanel.add(goJButton);
        workerJPanel.add(fibonacciJLabel);

        //add GUI components to the event-dispatching thread panel
        eventThreadJPanel.setBorder(new TitledBorder(
                new LineBorder(Color.BLACK), "Without SwingWorker"));
        eventThreadJPanel.add(nJLabel);
        eventThreadJPanel.add(nFibonacciJLabel);
        nextNumberJButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        long temp = n1+ n2;
                        n1 = n2;
                        n2 = temp;
                        ++count;

                        nJLabel.setText("Fibonacci of "+count+": ");
                        nFibonacciJLabel.setText(String.valueOf(n2));
                    }
                }
        );
        eventThreadJPanel.add(nextNumberJButton);

        add(workerJPanel);
        add(eventThreadJPanel);
        setSize(275,200);
        setVisible(true);
    }

    public static void main(String[] args){
        FibonacciNumbers application = new FibonacciNumbers();
        application.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
