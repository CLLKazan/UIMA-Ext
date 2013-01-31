/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
private[nfsm] class IdCounter {

  var lastId: Int = 0

  def nextId(): Int = {
    lastId += 1
    lastId
  }
}