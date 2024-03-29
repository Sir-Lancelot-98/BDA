import org.apache.spark.{SparkContext, SparkConf}

import org.apache.spark.rdd._

object tweetmining {

  val conf = new SparkConf()
    .setAppName("User mining")
    .setMaster("local[*]")

  val sc = new SparkContext(conf)

  def main(args: Array[String]) {
    var pathToFile = args(0)
    
    val tweets =
      sc.textFile(pathToFile).mapPartitions(TweetUtils.parseFromJson(_))

    val tweetsByUser = tweets.map(x => (x.user, x)).groupByKey()

    val numTweetsByUser = tweetsByUser.map(x => (x._1, x._2.size))

    val sortedUsersByNumTweets = numTweetsByUser.sortBy(_._2, ascending=false)

    sortedUsersByNumTweets.take(10).foreach(println)

  }
}

import com.google.gson._
	
object TweetUtils {
	case class Tweet (
		id : String,
		user : String,
		userName : String,
		text : String,
		place : String,
		country : String,
		lang : String
		)

	
	def parseFromJson(lines:Iterator[String]):Iterator[Tweet] = {
		val gson = new Gson
		lines.map(line => gson.fromJson(line, classOf[Tweet]))
	}
}

#####################################tweetmining.sbt################################

name := "tweetmining" 
version := "1.0" 
scalaVersion := "2.11.12" 
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.4"
