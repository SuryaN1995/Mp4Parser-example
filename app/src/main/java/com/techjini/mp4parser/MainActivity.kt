package com.techjini.mp4parser

import android.Manifest
import android.media.MediaScannerConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.annotation.NonNull
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import java.io.File
import java.io.RandomAccessFile
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handleJoinSongs()
    }

    private fun handleJoinSongs() {
        val input1 = Environment.getExternalStorageDirectory().toString()+ File.separator+"Download"+File.separator+"test1.mp3"
        val input2 = Environment.getExternalStorageDirectory().toString()+ File.separator+"Download"+File.separator+"test2.mp3"

        val arrayUri = arrayOf(input1,input2)

        val output = Environment.getExternalStorageDirectory().toString()+ File.separator+"Download"+File.separator+"output.mp3"
        val outFile = File(output)

        val moviesList = ArrayList<Movie>()

        arrayUri.forEach {
            moviesList.add(MovieCreator.build(it))
        }

        val audioTrack = LinkedList<Track>()

        moviesList.forEach {
            it.tracks.forEach {it1->
                if(it1.handler.equals("soun")){
                    audioTrack.add(it1)
                }
            }
        }

        val movie = Movie()
        movie.addTrack(AppendTrack(*audioTrack.toTypedArray()))

        val out = DefaultMp4Builder().build(movie)
        val fc = RandomAccessFile(outFile,"rw").channel
        out.writeContainer(fc)
        fc.close()

        MediaScannerConnection.scanFile(this, arrayOf(output), null, null)
    }
}
