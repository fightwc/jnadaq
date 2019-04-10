/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zxdaq;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import com.sun.jna.Pointer;

import jna.NiDaq;
import jna.NiDaqException;
import jna.Nicaiu;

/**
 *
 * @author Tony Lab
 */
public class ZxDaq {

    /**
     * @param args the command line arguments
     */
    /**
     * NiDaq middle layer to call NiDaq function.
     */
    private NiDaq daq = new NiDaq();
    private Pointer aiTask = null;
    public static final int DAQmx_Val_RSE = 10083;
//    public static final int DAQmx_Val_NRSE = 10078;
//    public static final int DAQmx_Val_Diff = 10106;
    public static final double SAMP_INTERVAL = 0.2;
    public static final double SAMPLE_RATE = 200;
    public static final int CHANNEL_COUNT = 16;

    public void initTask(boolean isNew, String[] devs) throws NiDaqException {
        boolean[] successed = new boolean[devs.length];
        if (isNew) {
            boolean devReady = false;
            while (!devReady) {
                try {
                    daq.resetDevice("Dev1");
                    aiTask = daq.createTask("AITask");
                    devReady = true;
                } catch (NiDaqException e) {
                    System.out.println(e.toString());
                }
            }
            for (int i = 0; i < devs.length; i++) {
                PlotFrame.debugOutput(devs[i]);
                while (!successed[i]) {
                    try {
                        Thread.sleep(100);
                        daq.createAIVoltageChannel(aiTask, devs[i], "", DAQmx_Val_RSE, 0.0, 10.0, Nicaiu.DAQmx_Val_Volts, null);
                        successed[i] = true;
                    } catch (NiDaqException | InterruptedException e) {
                        System.out.println(e.toString());
                    }
                }
            }
            daq.cfgSampClkTiming(aiTask, "", ZxDaq.SAMPLE_RATE, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_ContSamps, 1000);
        }
        daq.startTask(aiTask);
//        System.out.println("Started");
    }

    public double[] readAnalogueIn(int inputBufferSize) throws NiDaqException {
//        Integer read = new Integer(0);
        double[] buffer = new double[inputBufferSize * ZxDaq.CHANNEL_COUNT];
        DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
        IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[ZxDaq.CHANNEL_COUNT]);
        daq.readAnalogF64(aiTask, inputBufferSize, 1, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer, inputBufferSize * ZxDaq.CHANNEL_COUNT, samplesPerChannelRead);
//        System.out.println("Read");
//        System.out.println(Arrays.toString(buffer));
//        System.out.println(buffer.length);
        return buffer;
    }

    public void stopTask() throws NiDaqException {
        daq.stopTask(aiTask);
//        daq.clearTask(aiTask);
    }

    public static void main(String[] args) {
        new PlotFrame().setVisible(true);
    }

}
