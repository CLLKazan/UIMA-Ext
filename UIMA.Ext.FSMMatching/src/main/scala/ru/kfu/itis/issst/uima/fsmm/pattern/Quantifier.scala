/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.pattern

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
sealed trait Quantifier {

}

case class KleeneStar extends Quantifier {
  
}

case class KleenePlus extends Quantifier {
  
}