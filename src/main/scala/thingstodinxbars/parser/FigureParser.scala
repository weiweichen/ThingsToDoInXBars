package thingstodoinxbars.parser

import scala.xml.XML

class FigureParser {
  def parse(filename : String): Unit ={
    val xml = XML.loadFile(filename)
    println(xml)
  }

}
