package com.surveyhud
import android.Manifest
import android.content.pm.PackageManager
import android.hardware.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
class MainActivity:AppCompatActivity(),SensorEventListener{
 private lateinit var overlay:PointOverlayView;private lateinit var preview:PreviewView;private lateinit var status:TextView;private var points=emptyList<SurveyPoint>();private lateinit var sensors:SensorManager
 private var accel=FloatArray(3);private var mag=FloatArray(3);private var haveA=false;private var haveM=false
 private val permissions=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){startCamera()}
 private val openFile=registerForActivityResult(ActivityResultContracts.OpenDocument()){uri->uri?:return@registerForActivityResult;val text=contentResolver.openInputStream(uri)?.bufferedReader()?.use{it.readText()}?:return@registerForActivityResult;points=PointFileParser.parse(text);status.text=points.size.toString()+" points loaded";overlay.update(newPoints=points)}
 override fun onCreate(b:Bundle?){super.onCreate(b);sensors=getSystemService(SENSOR_SERVICE) as SensorManager
  val root=FrameLayout(this);preview=PreviewView(this);overlay=PointOverlayView(this);root.addView(preview,FrameLayout.LayoutParams(-1,-1));root.addView(overlay,FrameLayout.LayoutParams(-1,-1))
  val panel=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(20,20,20,20);setBackgroundColor(0x66000000)}
  val row=LinearLayout(this);val box=EditText(this).apply{hint="Point #";setTextColor(0xffffffff.toInt());setHintTextColor(0xffcccccc.toInt())};val find=Button(this).apply{text="FIND"};val load=Button(this).apply{text="LOAD POINTS"};status=TextView(this).apply{text="SurveyHUD v0.1";setTextColor(0xffffffff.toInt())}
  row.addView(box,LinearLayout.LayoutParams(0,-2,1f));row.addView(find);panel.addView(row);panel.addView(load);panel.addView(status);root.addView(panel,FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM));setContentView(root)
  load.setOnClickListener{openFile.launch(arrayOf("text/*","text/csv","application/octet-stream"))};find.setOnClickListener{val id=box.text.toString().trim();val ok=points.any{it.point.equals(id,true)};status.text=if(ok)"Active point "+id else "Point "+id+" not found";if(ok)overlay.update(active=id)}
  permissions.launch(arrayOf(Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION))
 }
 override fun onResume(){super.onResume();sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{sensors.registerListener(this,it,SensorManager.SENSOR_DELAY_UI)};sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also{sensors.registerListener(this,it,SensorManager.SENSOR_DELAY_UI)}}
 override fun onPause(){super.onPause();sensors.unregisterListener(this)}
 override fun onSensorChanged(e:SensorEvent){when(e.sensor.type){Sensor.TYPE_ACCELEROMETER->{e.values.copyInto(accel);haveA=true};Sensor.TYPE_MAGNETIC_FIELD->{e.values.copyInto(mag);haveM=true}};if(haveA&&haveM){val r=FloatArray(9);if(SensorManager.getRotationMatrix(r,null,accel,mag)){val o=FloatArray(3);SensorManager.getOrientation(r,o);var h=Math.toDegrees(o[0].toDouble()).toFloat();if(h<0)h+=360f;overlay.update(heading=h)}}}
 override fun onAccuracyChanged(s:Sensor?,a:Int){}
 private fun startCamera(){if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)return;val f=ProcessCameraProvider.getInstance(this);f.addListener({val provider=f.get();val p=androidx.camera.core.Preview.Builder().build().also{it.setSurfaceProvider(preview.surfaceProvider)};provider.unbindAll();provider.bindToLifecycle(this,CameraSelector.DEFAULT_BACK_CAMERA,p)},ContextCompat.getMainExecutor(this))}
}
