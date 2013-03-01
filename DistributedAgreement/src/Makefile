JC = javac
SRCDIR = src
BINDIR = bin
JFLAGS = -g
.SUFFIXES: .java .class
.java.class:
	@$(JC) $(JFLAGS) FailureDetectors/*.java
	@$(JC) $(JFLAGS) FailureDetectors/PerfectFailureDetector/*.java
JAVAS = $(wildcard FailureDetectors/*.java FailureDetectors/PerfectFailureDetector/*.java)
CLASSES = $(wildcard FailureDetectors/*.class FailureDetectors/PerfectFailureDetector/*.class)
default: classes
classes: $(JAVAS:.java=.class)
clean:
	./clean.sh
