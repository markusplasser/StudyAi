package org.example.manage;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;
import jcuda.runtime.*;

import static jcuda.driver.JCudaDriver.*;

public class JCudaRuntimeTest {
    public static void main(String[] args) {
        JCudaDriver.setExceptionsEnabled(true);
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        // Kernel laden
        CUmodule module = new CUmodule();
        cuModuleLoad(module, "add.ptx");
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "test");

        // Daten vorbereiten
        int n = 1024;
        float[] hostA = new float[n];
        float[] hostB = new float[n];
        float[] hostC = new float[n];
        for (int i = 0; i < n; i++) { hostA[i] = i; hostB[i] = i * 2; }

        CUdeviceptr devA = new CUdeviceptr();
        CUdeviceptr devB = new CUdeviceptr();
        CUdeviceptr devC = new CUdeviceptr();
        //AllocMem
        cuMemAlloc(devA, n * Sizeof.FLOAT);
        cuMemAlloc(devB, n * Sizeof.FLOAT);
        cuMemAlloc(devC, n * Sizeof.FLOAT);
        //MemcpyHostToDevice
        cuMemcpyHtoD(devA, Pointer.to(hostA), n * Sizeof.FLOAT);
        cuMemcpyHtoD(devB, Pointer.to(hostB), n * Sizeof.FLOAT);

        int blockSize = 256;
        int gridSize  = (n + blockSize - 1) / blockSize;
        Pointer params = Pointer.to(
                Pointer.to(devA), Pointer.to(devB),
                Pointer.to(devC), Pointer.to(new int[]{n})
        );
        cuLaunchKernel(function,
                gridSize, 1, 1,   // Grid-Dimensionen
                blockSize, 1, 1,  // Block-Dimensionen
                0, null,          // Shared Memory, Stream
                params, null
        );
        cuCtxSynchronize();

        // Ergebnis zurückkopiern
        cuMemcpyDtoH(Pointer.to(hostC), devC, n * Sizeof.FLOAT);
        System.out.println("c[0]  = " + hostC[0]);   // 0.0
        System.out.println("c[10] = " + hostC[10]);  // 30.0

        cuMemFree(devA); cuMemFree(devB); cuMemFree(devC);
    }
}
