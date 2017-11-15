package com.jerbotron_mac.spotisave.activities.home.adapters;

import com.gracenote.gnsdk.IGnAudioSource;
import com.jerbotron_mac.spotisave.activities.home.HomePresenter;

import java.nio.ByteBuffer;

public class AudioVisualizerAdapter implements IGnAudioSource {

    private HomePresenter presenter;
    private IGnAudioSource audioSource;
    private int numBitsPerSample;
    private int	numChannels;

    public AudioVisualizerAdapter(IGnAudioSource audioSource,
                                  HomePresenter presenter){
        this.audioSource = audioSource;
        this.presenter = presenter;
    }

    @Override
    public long sourceInit() {
        if ( audioSource == null ){
            return 1;
        }
        long retVal = audioSource.sourceInit();

        // get format information for use later
        if ( retVal == 0 ) {
            numBitsPerSample = (int)audioSource.sampleSizeInBits();
            numChannels = (int)audioSource.numberOfChannels();
        }

        return retVal;
    }

    @Override
    public long numberOfChannels() {
        return numChannels;
    }

    @Override
    public long sampleSizeInBits() {
        return numBitsPerSample;
    }

    @Override
    public long samplesPerSecond() {
        if ( audioSource == null ){
            return 0;
        }
        return audioSource.samplesPerSecond();
    }

    @Override
    public long getData(ByteBuffer buffer, long bufferSize) {
        if ( audioSource == null ){
            return 0;
        }

        long numBytes = audioSource.getData(buffer, bufferSize);

        if ( numBytes != 0 ) {
            // perform visualization effect here
            // Note: Since API level 9 Android provides android.media.audiofx.Visualizer which can be used to obtain the
            // raw waveform or FFT, and perform measurements such as peak RMS. You may wish to consider Visualizer class
            // instead of manually extracting the audio as shown here.
            // This sample does not use Visualizer so it can demonstrate how you can access the raw audio for purposes
            // not limited to visualization.
            presenter.setAmplitudePercent(rmsPercentOfMax(buffer, bufferSize, numBitsPerSample, numChannels));
        }

        return numBytes;
    }

    @Override
    public void sourceClose() {
        if ( audioSource != null ){
            audioSource.sourceClose();
        }
    }

    // calculate the rms as a percent of maximum
    private int rmsPercentOfMax(ByteBuffer buffer, long bufferSize, int numBitsPerSample, int numChannels) {
        double rms = 0.0;
        if ( numBitsPerSample == 8 ) {
            rms = rms8( buffer, bufferSize, numChannels );
            return (int)((rms*100)/(double)((double)(Byte.MAX_VALUE/2)));
        } else {
            rms = rms16( buffer, bufferSize, numChannels );
            return (int)((rms*100)/(double)((double)(Short.MAX_VALUE/2)));
        }
    }

    // calculate the rms of a buffer containing 8 bit audio samples
    private double rms8 ( ByteBuffer buffer, long bufferSize, int numChannels ) {

        long sum = 0;
        long numSamplesPerChannel = bufferSize/numChannels;

        for(int i = 0; i < numSamplesPerChannel; i+=numChannels)
        {
            byte sample = buffer.get();
            sum += (sample * sample);
        }

        return Math.sqrt( (double)(sum / numSamplesPerChannel) );
    }

    // calculate the rms of a buffer containing 16 bit audio samples
    private double rms16 ( ByteBuffer buffer, long bufferSize, int numChannels ) {

        long sum = 0;
        long numSamplesPerChannel = (bufferSize/2)/numChannels;	// 2 bytes per sample

        buffer.rewind();
        for(int i = 0; i < numSamplesPerChannel; i++)
        {
            short sample = Short.reverseBytes(buffer.getShort()); // reverse because raw data is little endian but Java short is big endian

            sum += (sample * sample);
            if ( numChannels == 2 ){
                buffer.getShort();
            }
        }

        return Math.sqrt( (double)(sum / numSamplesPerChannel) );
    }
}
