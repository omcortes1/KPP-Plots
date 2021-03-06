/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clas.analysis;

import java.awt.BorderLayout;
import org.clas.viewer.AnalysisMonitor;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.group.DataGroup;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;

/**
 *
 * @author devita
 */
public class HTCCmonitor  extends AnalysisMonitor {
        
    
    public HTCCmonitor(String name) {
        super(name);
        
        this.init();
    }

    @Override
    public void createHistos() {
        // initialize canvas and create histograms
        this.setNumberOfEvents(0);
        this.getAnalysisCanvas().getCanvas("canvas1").divide(2, 2);
        this.getAnalysisCanvas().getCanvas("canvas1").setGridX(false);
        this.getAnalysisCanvas().getCanvas("canvas1").setGridY(false);
        H1F summary = new H1F("summary","summary",6,1,7);
        summary.setTitleX("sector");
        summary.setTitleY("HTCC hits");
        summary.setFillColor(36);
        DataGroup sum = new DataGroup(1,1);
        sum.addDataSet(summary, 0);
        this.setAnalysisSummary(sum);
        H2F occADC = new H2F("occADC", "occADC", 8, 1, 9, 6, 1, 7);
        occADC.setTitleX("ring");
        occADC.setTitleY("sector");
        H2F occTDC = new H2F("occTDC", "occTDC", 8, 1, 9, 6, 1, 7);
        occTDC.setTitleX("ring");
        occTDC.setTitleY("sector");
        H2F adc = new H2F("adc", "adc", 100, 0, 5000, 48, 1, 49);
        adc.setTitleX("adc");
        adc.setTitleY("pmt");
        H2F tdc = new H2F("tdc", "tdc", 100, 0, 250, 48, 1, 49);
        adc.setTitleX("adc");
        adc.setTitleY("pmt");
           
        DataGroup dg = new DataGroup(2,1);
        dg.addDataSet(occADC, 0);
        dg.addDataSet(occTDC, 1);
        dg.addDataSet(adc, 2);
        dg.addDataSet(tdc, 3);
        this.getDataGroup().add(dg,0);
        
        // plotting histos
        this.getAnalysisCanvas().getCanvas("canvas1").cd(0);
        this.getAnalysisCanvas().getCanvas("canvas1").draw(this.getDataGroup().getItem(0).getH2F("occADC"));
        this.getAnalysisCanvas().getCanvas("canvas1").cd(1);
        this.getAnalysisCanvas().getCanvas("canvas1").draw(this.getDataGroup().getItem(0).getH2F("occTDC"));
        this.getAnalysisCanvas().getCanvas("canvas1").cd(2);
        this.getAnalysisCanvas().getCanvas("canvas1").draw(this.getDataGroup().getItem(0).getH2F("adc"));
        this.getAnalysisCanvas().getCanvas("canvas1").cd(3);
        this.getAnalysisCanvas().getCanvas("canvas1").draw(this.getDataGroup().getItem(0).getH2F("tdc"));
        this.getAnalysisCanvas().getCanvas("canvas1").update();
        this.getAnalysisView().getView().repaint();
        this.getAnalysisView().update();

    }

    public void drawDetector() {
        this.getAnalysisView().setName("HTCC");
        this.getAnalysisView().updateBox();
    }

    @Override
    public void init() {
        this.getAnalysisPanel().setLayout(new BorderLayout());
//        this.drawDetector();
//        JSplitPane   splitPane = new JSplitPane();
//        splitPane.setLeftComponent(this.getDetectorView());
//        splitPane.setRightComponent(this.getDetectorCanvas());
        this.getAnalysisPanel().add(this.getAnalysisCanvas(),BorderLayout.CENTER); 
        this.createHistos();
    }
        
    @Override
    public void processEvent(DataEvent event) {
        // process event info and save into data group
        if(event.hasBank("HTCC::adc")==true){
	    DataBank bank = event.getBank("HTCC::adc");
	    int rows = bank.rows();
	    for(int loop = 0; loop < rows; loop++){
                int sector  = bank.getByte("sector", loop);
                int layer   = bank.getByte("layer", loop);
                int comp    = bank.getShort("component", loop);
                int order   = bank.getByte("order", loop);
                int adc     = bank.getInt("ADC", loop);
                float time  = bank.getFloat("time", loop);
//                System.out.println("ROW " + loop + " SECTOR = " + sector + " LAYER = " + layer + " COMPONENT = " + comp + " ORDER + " + order +
//                      " ADC = " + adc + " TIME = " + time); 
                if(adc>0) this.getDataGroup().getItem(0).getH2F("occADC").fill(((layer-1)*2+comp)*1.0,sector*1.0);
                if(time>0) this.getDataGroup().getItem(0).getH2F("occTDC").fill(((layer-1)*2+comp)*1.0,sector*1.0);
                if(adc>0) this.getDataGroup().getItem(0).getH2F("adc").fill(adc*1.0,((sector-1)*8+(layer-1)*2+comp)*1.0);
                if(time>0) this.getDataGroup().getItem(0).getH2F("tdc").fill(time,((sector-1)*8+(layer-1)*2+comp)*1.0);
                this.getAnalysisSummary().getH1F("summary").fill(sector*1.0);
	    }
    	}
        
    }

    @Override
    public void resetEventListener() {
        System.out.println("Resetting HTCC histogram");
        this.createHistos();
    }

    @Override
    public void timerUpdate() {

    }


}
