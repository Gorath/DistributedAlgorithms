JC = javac
SRCDIR = src
BINDIR = bin
JFLAGS = -g -Xlint:unchecked
.SUFFIXES: .java .class
.java.class:
	@$(JC) $(JFLAGS) FailureDetectors/*.java
	@$(JC) $(JFLAGS) FailureDetectors/PerfectFailureDetector/*.java
	@$(JC) $(JFLAGS) FailureDetectors/EventuallyPerfectFailureDetector/*.java
	@$(JC) $(JFLAGS) FailureDetectors/LeaderElector/*.java
JAVAS = $(wildcard FailureDetectors/*.java FailureDetectors/PerfectFailureDetector/*.java FailureDetectors/EventuallyPerfectFailureDetector/*.java FailureDetectors/LeaderElector/*.java)
CLASSES = $(wildcard FailureDetectors/*.class FailureDetectors/PerfectFailureDetector/*.class FailureDetectors/EventuallyPerfectFailureDetector/*.class FailureDetectors/LeaderElector/*.class )

default: classes
classes: $(JAVAS:.java=.class)
clean:
	./clean.sh
	rm -f *.out
	rm -f *.err
