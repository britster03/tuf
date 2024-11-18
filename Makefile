# Makefile for playfair cipher code

# variables
JAVAC = javac                  # java compiler
JAVA = java                    # java runtime
SOURCE = PlayFairCipher10.java # source file
CLASS = PlayFairCipher10.class # compiled class file
MAIN_CLASS = PlayFairCipher10  # main class name

# default target: compile the program
all: $(CLASS)

# rule to compile the Java source file
$(CLASS): $(SOURCE)
	$(JAVAC) $(SOURCE)

# to get the outputs, run the program with given arguments
# use the following: make run ARG1=<keyword> ARG2=<1 for encrypt / 0 for decrypt>
run: $(CLASS)
	$(JAVA) $(MAIN_CLASS) $(ARG1) $(ARG2)

# clean up compiled class files
clean:
	rm -f *.class

# to prevent conflicts with files of the same name
.PHONY: all run clean


 # example test case usage

# ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ make run ARG1=commom ARG2=1
# java                     PlayFairCipher10   commom 1
# refined keyword: com
# constructed playfair cipher matrix:
# c o m a b
# d e f g h
# ij k l n p
# q r s t u
# v w x y z
# please enter the plaintext:
# gdddcgs
# formatted text: gddxdcgs
# resulting text: hefvidft

# ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ make run ARG1=commom ARG2=0
# java                     PlayFairCipher10   commom 0
# refined keyword: com
# constructed playfair cipher matrix:
# c o m a b
# d e f g h
# ij k l n p
# q r s t u
# v w x y z
# please enter the ciphertext:
# hefvidft
# formatted text: hefvidft
# resulting text: gdddcgs