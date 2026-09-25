package com.surveyhud
object PointFileParser {
 fun parse(text:String):List<SurveyPoint> = text.lineSequence().mapNotNull { raw ->
  val p=raw.trim().split(",").map{it.trim()}
  if(p.size<5) return@mapNotNull null
  val n=p[1].toDoubleOrNull()?:return@mapNotNull null
  val e=p[2].toDoubleOrNull()?:return@mapNotNull null
  val z=p[3].toDoubleOrNull()?:return@mapNotNull null
  SurveyPoint(p[0],n,e,z,p.drop(4).joinToString(","))
 }.toList()
}
