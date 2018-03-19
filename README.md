WRPN is [Emmet Gray's](http://www.emmet-gray.com/) wonderful little
calculator, modeled after the [HP 16-C](https://en.wikipedia.org/wiki/HP-16C).
The original is available at 
[https://en.wikipedia.org/wiki/HP-16C](https://en.wikipedia.org/wiki/HP-16C).
As of this writing, I'm planning to take the Android version, and modify it
to use fonts to draw the key labels, so they come out nice and crisp on
big screens.

In order to track the changes, I started with the original source, downloaded
on March 19, 2018 from
[https://en.wikipedia.org/wiki/HP-16C](https://en.wikipedia.org/wiki/HP-16C).
I applied a couple of little changes:

   *  I re-created the gradle build from scratch, because that was easier
      than figuring out why IDEA kept telling me to convert to gradle.

   *  There was a stray ">>" instead of ">" in one of the XML files.
      I guess the Android SDK got a little more picky over the years.

   *  I made all the files have Unix-style end-of-line, and I fixed up a
      few files that were assuming tab stops at 4 before doing the initial
      git commit.

Essentially, I did the minimum necessary to get the program to build and
run, with unix-style files (since the commit was from unix).

I don't have any predictions as to when I might work on this.  For now, I just
wanted to make a repository before I start tinkering.  It's nice to have a
couple of projects on the back burner :-)
