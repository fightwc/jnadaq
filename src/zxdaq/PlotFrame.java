/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zxdaq;

import java.awt.Color;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import jna.NiDaqException;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

/**
 *
 * @author Tony Lab
 */
public class PlotFrame extends javax.swing.JFrame {

    private XYChart chart = new XYChartBuilder().width(800).height(560).build();
    final private String dataNameA = "data_A";
    private LinkedList<Double> ydata_A = new LinkedList<>();
    private ScheduledExecutorService ses = new ScheduledThreadPoolExecutor(1);
    final private ZxDaq zdaq=new ZxDaq();
    private volatile int counter;
    private boolean completelyNew=true;

    /**
     * Creates new form PlotFrame
     */
    public PlotFrame() {
        chart.getStyler().setPlotMargin(2)
                .setChartBackgroundColor(Color.white).setLegendVisible(false);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPlot = new XChartPanel<XYChart>(chart);
        pnlBtns = new javax.swing.JPanel();
        btnStart = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        btnClr = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout pnlPlotLayout = new javax.swing.GroupLayout(pnlPlot);
        pnlPlot.setLayout(pnlPlotLayout);
        pnlPlotLayout.setHorizontalGroup(
            pnlPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        pnlPlotLayout.setVerticalGroup(
            pnlPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );

        getContentPane().add(pnlPlot, java.awt.BorderLayout.CENTER);

        pnlBtns.setPreferredSize(new java.awt.Dimension(800, 40));
        pnlBtns.setLayout(new java.awt.GridLayout());

        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });
        pnlBtns.add(btnStart);

        btnStop.setText("Stop");
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        pnlBtns.add(btnStop);

        btnClr.setText("Clear");
        btnClr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClrActionPerformed(evt);
            }
        });
        pnlBtns.add(btnClr);

        getContentPane().add(pnlBtns, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClrActionPerformed
        ydata_A.clear();
        if (!chart.getSeriesMap().isEmpty()) {
            chart.removeSeries(dataNameA);
        }
        pnlPlot.repaint();
        pnlPlot.revalidate();
    }//GEN-LAST:event_btnClrActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed

        try {
            counter = 0;
            ydata_A.clear();
            zdaq.initTask(completelyNew);
            completelyNew=false;
            ses.scheduleAtFixedRate(new Update(), 500, 50, TimeUnit.MILLISECONDS);
        } catch (NiDaqException e) {
            System.out.println(e.toString());
        }


    }//GEN-LAST:event_btnStartActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        try{
        ses.shutdownNow();
        zdaq.stopTask();
        }catch(NiDaqException e){
            System.out.println(e.toString());
        }
    }//GEN-LAST:event_btnStopActionPerformed

    private class Update implements Runnable {

        @Override
        public void run() {
            try {
                if (counter > 500) {
                    ses.shutdownNow();
                    zdaq.stopTask();
                } else {
                    counter++;
                    double[] data = zdaq.readAnalogueIn((int) (ZxDaq.sampRate * ZxDaq.timeSPANSec));
                    if (data != null) {
                        for (double d : data) {
                            ydata_A.add(d);
                        }
                        if (chart.getSeriesMap().isEmpty()) {
                            chart.addSeries(dataNameA, null, ydata_A, null);
                        }
                        chart.updateXYSeries(dataNameA, null, ydata_A, null);
//                        System.out.println(counter + "," + ydata_A.size());
                        pnlPlot.repaint();
                        pnlPlot.revalidate();

                    } else {
                        System.out.println("Error");
                    }
                }
            } catch (NiDaqException e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PlotFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlotFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlotFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlotFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PlotFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClr;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel pnlBtns;
    private javax.swing.JPanel pnlPlot;
    // End of variables declaration//GEN-END:variables
}
