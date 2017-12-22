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
    public static final int DAQmx_Val_RSE = (int) 10083;
    public static final double timeSPANSec = 0.2;
    public static final double sampRate = 10.0;

    public void initTask(boolean isNew) throws NiDaqException {
        if (isNew) {
            aiTask = daq.createTask("AITask");
            daq.createAIVoltageChannel(aiTask, "/Dev1/ai3", "", DAQmx_Val_RSE, 0.0, 5.0, Nicaiu.DAQmx_Val_Volts, null);
//            daq.createAIVoltageChannel(aiTask, "/Dev1/ai2", "", DAQmx_Val_RSE, 0.0, 5.0, Nicaiu.DAQmx_Val_Volts, null);
            daq.cfgSampClkTiming(aiTask, "", ZxDaq.sampRate, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_ContSamps, 1000);
        }
        daq.startTask(aiTask);
    }

    public double[] readAnalogueIn(int inputBufferSize) throws NiDaqException {
        Integer read = new Integer(0);
        double[] buffer = new double[inputBufferSize];
        DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
        IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[]{read});
        daq.readAnalogF64(aiTask, inputBufferSize, 1, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer, inputBufferSize, samplesPerChannelRead);
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
