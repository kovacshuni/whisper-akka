# Whisper Akka

Testing speed of actors and the actor system. Passing a message through N actors.

Hi,

I the Whisper Go application in a Scala program using the Akka library. I wanted to see if the speed of akka actors could be matched to the speed of those Go channels.

Seems like it works well, the message is passed through 100k actors in 57ms. It is true, there is an overhead, until these actors get created, lasted 715ms. I ran them on a single thread. The limit would be mostly memory-biased, actors usually take up 300bytes, multiply that with 100k, gives 28MB, a million actors would be 280MB, so on. They would run almost as fast, just would require larger -Xmx :)

After that i tried the same thing using futures, to see how fast these other constructs behave. They behaved better, no overhead in setup, same action speed. Weird, by documentation, actors should be the coolest, not futures.

That's it. Thought you might find interesting/informative.

If you want to try it out:

`sbt "run 100000"`
