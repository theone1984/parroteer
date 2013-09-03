package com.dronecontrol.perceptual.components.filters;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

public class KalmanFilter {

    private SimpleMatrix F;
    private SimpleMatrix H;

    private SimpleMatrix x;
    private SimpleMatrix P;

    public void configure(DenseMatrix64F F, DenseMatrix64F H) {
        this.F = new SimpleMatrix(F);
        this.H = new SimpleMatrix(H);
    }

    public void setState(DenseMatrix64F x, DenseMatrix64F P) {
        this.x = new SimpleMatrix(x);
        this.P = new SimpleMatrix(P);
    }

    public void predict() {
        // x = F x
        x = F.mult(x);

        // P = F P F' + Q
        P = F.mult(P).mult(F.transpose());
    }

    public void update(DenseMatrix64F _z, DenseMatrix64F _R) {
        // a fast way to make the matrices usable by SimpleMatrix
        SimpleMatrix z = SimpleMatrix.wrap(_z);
        SimpleMatrix R = SimpleMatrix.wrap(_R);

        // y = z - H x
        SimpleMatrix y = z.minus(H.mult(x));

        // S = H P H' + R
        SimpleMatrix S = H.mult(P).mult(H.transpose()).plus(R);

        // K = PH'S^(-1)
        SimpleMatrix K = P.mult(H.transpose().mult(S.invert()));

        // x = x + Ky
        x = x.plus(K.mult(y));

        // P = (I-kH)P = P - KHP
        P = P.minus(K.mult(H).mult(P));
    }

    public DenseMatrix64F getState() {
        return x.getMatrix();
    }

    public DenseMatrix64F getCovariance() {
        return P.getMatrix();
    }
}