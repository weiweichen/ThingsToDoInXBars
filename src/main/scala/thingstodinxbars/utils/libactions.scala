package thingstodinxbars.utils

import java.io._
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

import pl.project13.scala.rainbow._
import thingstodinxbars.utils.{TXBException, QFEException}

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Map}
import scala.sys.process._
import scala.xml._


/**
  * This object contains all the library methods needed for
  * actions file extended from treeActions class
*/
package object libactions {
    //Helper Functions
    /****************************************************************************************/

    /**Linux command to be run are passed to this method */
    def runCommand(command: String, directory: File,exitOnError:Boolean = false): (String, String) = {

      val errbuffer = new StringBuffer()
      val outbuffer = new StringBuffer()

      val logger = ProcessLogger(
          (o: String) => outbuffer.append(o + "\n"),
          (e: String) => errbuffer.append(e + "\n"))

      //run the command
      sys.process.stringSeqToProcess(Seq("/bin/bash", "-c", "cd " +
              directory.getAbsolutePath + ";" + command)) ! logger

      //Print the error in red color and exit
      if(errbuffer.length > 0) {
        println(errbuffer.toString.red)
        if(exitOnError) {
            throw TXBException(errbuffer.toString)
        }
      }

      return (outbuffer.toString(), errbuffer.toString())
    }

    /**returns number of lines in a file*/
    def getLineNumber(file: String): Integer = {
       val src = io.Source.fromFile(file)
       try {
         src.getLines.size
       } catch {
         case error: FileNotFoundException => -1
         case error: Exception => -1
       }
       finally {
         src.close()
       }
    }

    /**appends to existing File*/
    def appendFile(file:String, line:String) = {
        val fileToAppend = new FileWriter(file, true)
        try {
            fileToAppend.write(line)
        }
        finally fileToAppend.close()
    }

    /**Check if a particular string is present in File*/
    def fileContainsString(file:String, chk:String) = {
       val src = io.Source.fromFile(file)
       try {
         src.mkString.contains(chk)
       } catch {
         case error: FileNotFoundException => false
         case error: Exception => false
       }
       finally {
         src.close()
       }
    }


    /** generic method to error out if key does not exist  */
    def getWithError[A,B] (key:B, keyVal: Option[A]):A = {
        var retvl:A = null.asInstanceOf[A]
        keyVal match {
            case Some(mapVal) => retvl = mapVal
            case None => {
                TXBDebug(("Error:Cannot find key " + key).red)
                throw TXBException("Error:Cannot find key " + key)
            }
        }
        retvl
    }

    /**
      * generic method to warn and return null if key does not exist
    */
    def getWithWarning[A,B] (key:B, keyVal: Option[A]):A = {
        var retvl:A = ().asInstanceOf[A]
        keyVal match {
            case Some(mapVal) => retvl = mapVal
            case None => {
                TXBDebug("Warning:Cannot find key " + key.toString)
            }
        }
        retvl
    }

    /**
    *   generic method to return null if key does not exist
    */
    def getSilent[A,B] (key:B, keyVal: Option[A]):A = {
        var retvl:A = null.asInstanceOf[A]
        keyVal match {
            case Some(mapVal) => retvl = mapVal
            case None => {}
        }
        retvl
    }


    /**Extract FS type out of type string*/
    def convertFSType(fsType:String):String = {
        val rawType = """\s*dat_t<(\w+)>\s*""".r
        rawType.findFirstIn(fsType) match {
            case Some(rawType(inType)) => "uint" + inType + "_t"
            case None => fsType
        }
    }

    /**Loads shell config file*/
    def loadShellConfig (xmlPath: String): scala.xml.Elem = {
        // Loading XML config file
        var shellConfig: scala.xml.Elem = null
        try {
            shellConfig = XML.loadFile(xmlPath)
        }
        catch {
            case ex: java.io.FileNotFoundException => {
                return null
            }
        }
        return shellConfig
    }

    /**Loads shell config file and extract only the network config*/
    def loadShellNetworkConfig(xmlPath: String): Map[String, String] = {
        // Load the shell config XML object
        val shellConfig = loadShellConfig(xmlPath: String)

        if (shellConfig == null)
            return Map[String, String]()

        // If the networking is
        if ((shellConfig \ "Network").toString() == "")
            return Map[String, String]()

        var networkConfigs = Map[String, String]()
        // Loading network config data
        val ethSrcFormatted = (shellConfig \ "Network" \ "ETHSRC").text
        val ethDstFormatted = (shellConfig \ "Network" \ "ETHDST").text
        val ipSrcFormatted = (shellConfig \ "Network" \ "IPSRC").text
        val ipDstFormatted = (shellConfig \ "Network" \ "IPDST").text
        val udpPortSrc = (shellConfig \ "Network" \ "UDPPORTSRC").text
        val udpPortDst = (shellConfig \ "Network" \ "UDPPORTDST").text

        // Extracting and reformatting ETHSRC
        if (ethSrcFormatted == "") {
            networkConfigs += ("ETHSRC" -> "")
        }
        else {
            val ethSrc = ethSrcFormatted.split(":").mkString("")
            if (ethSrc != "")
                networkConfigs += ("ETHSRC" -> ("h" + ethSrc))
        }

        // Extracting and reformatting ETHDST
        if (ethDstFormatted == "") {
            networkConfigs += ("ETHDST" -> "")
        }
        else {
            val ethDst = ethDstFormatted.split(":").mkString("")
            if (ethDst != "")
                networkConfigs += ("ETHDST" -> ("h" + ethDst))
        }

        // Extracting and reformatting IPSRC
        if (ipSrcFormatted == "") {
            networkConfigs += ("IPSRC" -> "")
        }
        else {
            val ipSrc = ipSrcFormatted.split("\\.").map(s => String.format("%02X", s.toInt: Integer)).mkString("")
            if (ipSrc != "")
                networkConfigs += ("IPSRC" -> ("h" + ipSrc))
        }

        // Extracting and reformatting IPDST
        if (ipDstFormatted == "") {
            networkConfigs += ("IPDST" -> "")
        }
        else {
            val ipDst = ipDstFormatted.split("\\.").map(s => String.format("%02X", s.toInt: Integer)).mkString("")
            if (ipDst != "")
                networkConfigs += ("IPDST" -> ("h" + ipDst))
        }

        // Extracting and reformatting UDPSRCPORT
        if (udpPortSrc == "") {
            networkConfigs += ("UDPPORTSRC" -> "")
        }
        else {
            networkConfigs += ("UDPPORTSRC" -> ("h" + Integer.toHexString(udpPortSrc.toInt)))
        }

        // Extracting and reformatting UDPDSTPORT
        if (udpPortDst == "") {
            networkConfigs += ("UDPPORTDST" -> "")
        }
        else {
            networkConfigs += ("UDPPORTDST" -> ("h" + Integer.toHexString(udpPortDst.toInt)))
        }
        return networkConfigs
    }

    /** Checks if a certain attribute is present in XML Node Tree
    */
    def attributeEquals(value: String)(node: xml.Node) =  {
        node.attributes.exists(_.value.text == value)
    }

   /**
    * Show a compiler error message and exit
    *
    * @param prefix       the compiler related prefix
    * @param errorMessage the message
    */
    def compilerError (prefix: String, errorMessage: String) = {
        val t = new Throwable
        t.getStackTrace.map(s => println(s))
        System.err.println(prefix.red + "error: ".red + errorMessage.red)
        throw new TXBException(prefix + " " + errorMessage)
    }

    /**
     * Show a compiler warning message and exit
     *
     * @param prefix       the compiler related prefix
     * @param errorMessage the message
     */
    def compilerWarning (prefix: String, errorMessage: String) = {
        println(prefix.yellow + "warning: ".yellow + errorMessage.yellow)
    }

    /**
     * Show a compiler message
     *
     * @param prefix       the compiler related prefix
     * @param errorMessage the message
     */
    def compilerMessage (prefix: String, errorMessage: String) = {
        TXBDebug(prefix.white + ": ".white + errorMessage.white)
    }

   /**
     * Return empty ArrayBuffer in case Map is empty
     */
    def getMapIfEmpty(key:String,
                      keyVal:Option[ArrayBuffer[(String, String, String)]] ) = {
        keyVal match {
            case Some(mapVal) => mapVal
            case None => ArrayBuffer[(String, String, String)]()
        }
    }

    /**
     *  Return empty Map of ArrayBuffer in case Map is empty
     */
    def getMapOfMap(key:String,
                    keyVal:Option[collection.mutable.Map[String, ArrayBuffer[(String)]] ] ) = {
        keyVal match {
            case Some(mapVal) => mapVal
            case None => collection.mutable.Map[String, ArrayBuffer[(String)]]()
        }
    }

    def writeStringToFile(filePath : String, fileString : String) : Unit = {
      TXBDebug(("Query CodeGen: write to file " + filePath).green)
      val file = new PrintWriter(new File(filePath))
      file.write(fileString)
      file.close()
    }

    def TXBDebug(info : Any) = {
      if (configs.enableDebug)
        println(info)
    }
}

object configs {
   var enableDebug = false
}