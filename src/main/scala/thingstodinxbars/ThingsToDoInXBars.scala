package thingstodinxbars

import thingstodinxbars.utils._
import thingstodoinxbars.parser.FigureParser

object ThingsToDoInXBars {

  def main(args: Array[String]): Unit = {
    val argList = args.toList
    type OptionMap = Map[String, Any]

    def parseOptions(map : OptionMap, list : List[String]) : OptionMap = {
      list match {
        case Nil => map
        case "-xmlroot"::filename::tail => parseOptions(map ++ Map("queryPlan" -> filename), tail)
        case "-debug"::debug::tail => parseOptions(map ++ Map("debug" -> debug.toBoolean), tail)
        case option::tail =>
          libactions.compilerError(Constants.TXB_FIGURE_PARSING, "Unknown CLI options:" + option)
      }
    }

    val optionMap = parseOptions(Map(), argList)

    configs.enableDebug = optionMap.getOrElse("debug", true).asInstanceOf[Boolean]


       val figureParser = new FigureParser
       figureParser.parse("")
  }
}