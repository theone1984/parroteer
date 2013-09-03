package com.dronecontrol.intelcontrol.entry;

import com.dronecontrol.perceptual.components.filters.BilateralFilter;
import com.dronecontrol.perceptual.data.body.Coordinate;
import com.dronecontrol.perceptual.data.body.Hands;
import com.dronecontrol.perceptual.data.events.DetectionData;
import com.dronecontrol.perceptual.data.events.HandsDetectionData;
import com.dronecontrol.perceptual.listeners.DetectionListener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class DeviationPlotter implements DetectionListener<Hands> {
    private int i = 0;

    private int x = 0;

    private long startTime;

    private final PrintWriter writer;

    public DeviationPlotter() {
        startTime = new Date().getTime();

        try {
            String fileName = String.format("sigma_t-%d-sigma_u-%d", BilateralFilter.SIGMA_T, (int) (BilateralFilter.SIGMA_U * 100.0));
            if (Files.exists(Paths.get("C:/Users/Thomas Endres/Dropbox/Perceptual/" + fileName))) {
                Files.delete(Paths.get("C:/Users/Thomas Endres/Dropbox/Perceptual/" + fileName));
            }
            writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/Thomas Endres/Dropbox/Perceptual/" + fileName, true)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void dispose() {
        writer.close();
    }

    @Override
    public void onDetection(DetectionData<Hands> data) {
        x++;
        if (x % 10 == 0) {
            double timePassed = new Date().getTime() - startTime;
            double fps = ((double) x) / (timePassed / 1000.0);

            System.out.println(String.format("%.1f", fps));
        }

        HandsDetectionData handsDetectionData = (HandsDetectionData) data;
        if (!handsDetectionData.getLeftHand().isActive()) {
            return;
        }


        i++;
        Coordinate coord = handsDetectionData.getLeftHand().getCoordinate();
        Coordinate uCoord = handsDetectionData.getLeftHand().getUnsmoothedCoordinate();

        String text = String.format("%d\t%.7f\t%.7f\t%.7f\t%.7f\t%.7f\t%.7f", i, coord.getX(), coord.getY(), coord.getZ(), uCoord.getX(), uCoord.getY(), uCoord.getZ());
        writer.println(text);
    }


}
