/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.util

/**
 * @author Rinat Gareev
 *
 */
trait Buildable {

  private var built = false

  def finishBuild() { built = true }
  def isBuilt = built

}