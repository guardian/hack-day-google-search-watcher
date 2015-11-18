package views

object Truncator {
  def truncateAtSlash(input: String, length: Int): String = truncateAt(input, length, '/')

  def truncateAtSpace(input: String, length: Int): String = truncateAt(input, length, ' ')

  private def truncateAt(input: String, length: Int, char: Char): String = {
    var slashIndex = input.indexOf(char, length)
    if(slashIndex > -1) input.substring(0, slashIndex) + "…" else input
  }
}
