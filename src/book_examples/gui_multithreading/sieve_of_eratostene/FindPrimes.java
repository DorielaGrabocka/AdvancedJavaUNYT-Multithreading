/*******************************************************************************
 * Copyright (c) 28/12/2020. Author Doriela Grabocka. All rights reserved.
 ******************************************************************************/

package book_examples.gui_multithreading.sieve_of_eratostene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FindPrimes extends JFrame {
    private final JTextField highestPrimeJTextField = new JTextField();
    private final JButton getPrimesJButton = new JButton("Get Primes");
    private final JButton cancelJButton = new JButton("Cancel");
    private final JTextArea displayPrimesJTextArea = new JTextArea();
    private final JProgressBar progressJProgressBar = new JProgressBar();
    private final JLabel statusJlabel = new JLabel();
    private PrimeCalculator calculator;

    public FindPrimes(){
        super("Finding Primes with SwingWorker");
        setLayout(new BorderLayout());

        //initialize panel to get a number from the user
        JPanel northJPanel = new JPanel();
        northJPanel.add(new JLabel("Finding primes less than: "));
        highestPrimeJTextField.setColumns(5);
        northJPanel.add(highestPrimeJTextField);
        getPrimesJButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        progressJProgressBar.setValue(0);
                        displayPrimesJTextArea.setText("");
                        statusJlabel.setText("");//clear JText Area

                        int number;

                        try{
                            number = Integer.parseInt(highestPrimeJTextField.getText());
                        }
                        catch(NumberFormatException ex){
                            statusJlabel.setText("Enter an integer");
                            return;
                        }

                        calculator = new PrimeCalculator(number, displayPrimesJTextArea,
                                getPrimesJButton, cancelJButton, statusJlabel);

                        calculator.addPropertyChangeListener(
                                new PropertyChangeListener() {
                                    @Override
                                    public void propertyChange(PropertyChangeEvent evt) {
                                        if(evt.getPropertyName().equals("progress")){
                                            int newVal = (Integer) evt.getNewValue();
                                            progressJProgressBar.setValue(newVal);
                                        }
                                    }
                                }//end of anonymous inner class
                        );//end call to addPropertyChangeListener

                        getPrimesJButton.setEnabled(false);
                        cancelJButton.setEnabled(true);

                        calculator.execute();//execute the PrimeCalculator object
                    }
                }
        );
        northJPanel.add(getPrimesJButton);

        displayPrimesJTextArea.setEditable(false);
        add(new JScrollPane(displayPrimesJTextArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));

        JPanel southJPanel = new JPanel(new GridLayout(1,3,10,10));
        cancelJButton.setEnabled(false);
        cancelJButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        calculator.cancel(true);
                    }
                }//end anonymous inner class
        );//end call to  addActionListener

        southJPanel.add(cancelJButton);
        progressJProgressBar.setStringPainted(true);
        southJPanel.add(progressJProgressBar);
        southJPanel.add(statusJlabel);

        add(northJPanel, BorderLayout.NORTH);
        add(southJPanel, BorderLayout.SOUTH);
        setSize(350,300);
        setVisible(true);
    }

    public static void main(String[] args){
        FindPrimes application = new FindPrimes();
        application.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
