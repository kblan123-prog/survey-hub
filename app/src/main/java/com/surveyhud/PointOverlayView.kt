package com.surveyhud
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
class PointOverlayView(context:Context,attrs:AttributeSet?=null):View(context,attrs){
 var points:List<SurveyPoint> = emptyList(); var activePoint:String?=null; var headingDeg:Float=0f
 private val paint=Paint(Paint.ANTI_ALIAS_FLAG).apply{textAlign=Paint.Align.CENTER;typeface=Typeface.DEFAULT_BOLD}
 fun update(newPoints:List<SurveyPoint>?=null,active:String?=activePoint,heading:Float?=null){newPoints?.let{points=it};activePoint=active;heading?.let{headingDeg=it};invalidate()}
 override fun onDraw(c:Canvas){super.onDraw(c);val center=width/2f;paint.style=Paint.Style.FILL;paint.color=Color.WHITE;paint.textSize=32f;c.drawText("HDG "+headingDeg.toInt()+" deg",center,55f,paint)
  if(points.isEmpty()){paint.textSize=27f;c.drawText("LOAD P,N,E,Z,D POINT FILE",center,height*.55f,paint);return}
  val active=points.firstOrNull{it.point.equals(activePoint,true)}
  if(active!=null){paint.color=Color.GREEN;c.drawCircle(center,height*.43f,24f,paint);paint.textSize=38f;c.drawText("POINT "+active.point,center,height*.43f+70f,paint);paint.textSize=25f;c.drawText(active.description,center,height*.43f+108f,paint)}
  paint.color=Color.WHITE;paint.textSize=21f;points.take(12).forEachIndexed{i,p->if(p.point!=activePoint){val span=(width.coerceAtLeast(180)-90);val x=45f+((i*113)%span);val y=160f+((i*89)%(height.coerceAtLeast(500)-330));c.drawCircle(x,y,8f,paint);c.drawText(p.point,x,y-15f,paint)}}
 }
}
