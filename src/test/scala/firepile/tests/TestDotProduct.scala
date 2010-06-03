package firepile.tests

import firepile._
import firepile.Device
import firepile.Spaces._
import firepile.util.BufferBackedArray._

object TestDotProduct {
  def main(args: Array[String]) = {
    implicit val gpu: Device = firepile.gpu

    val dataSize = if (args.length > 0) args(0).toInt else 1000

    val b1 = BBArray.tabulate(dataSize)(_.toFloat)
    val b2 = BBArray.tabulate(dataSize)(_.toFloat).reverse

    println("cl bbarray dot product");
    {
      val c: Float = time {
        val result = spawn {
            val b = (b1 zip b2)
            val f = (p: (Float,Float)) => p._1 * p._2
            val m = b.map(f)
            val r = m.reduceLeft(_+_)
            r
        }
        result.force
      }
      println("c = " + c)
      val correct = (b1 zip b2).map((p: (Float,Float)) => p._1 * p._2).reduceLeft(_+_)
      println("correct sum = " + correct)
      assert(c == correct)
    }
  }
}