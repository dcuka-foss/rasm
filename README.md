# Overview
RASM is a simple assembler for the RISC-V assembly language.  I wrote this while assisting someone in learning assembly language and it was easy and fun to implement!  RASM reads a source file and makes 2 passes through the instructions to create an executable version of the program.  The first pass creates a symbol table of registers and labels and the second pass does the actual translation into the executable format.

## Key features
* Declare mnemonic names for registers
* Declare and reference labels for program control
* Extensible to add new pseudo-instructions to the list of OP codes (e.g. goto)
* Predefined memory locations for TEXTIO:, CONSOLE:, and  BITMAP: for the simulator
* Code printout containing the hex code, address, instruction and source line to help understand the translation 

# References
* [RISC-V simulator](http://tice.sea.eseo.fr/riscv/) - this is a really neat site that emulates the RISV-V architecture and allows for testing and tracing of programs.  The **a.hex** file generated by RASM is compatible with this site.

# Extensions
Sometimes it can be helpful to extend a language by adding new keywords.  RASM includes such a capability with a plugin Extension interface.  Extensions are written in java and must be a subclass of dac.rasm.Extension.  An individual extension can be added with '+x classname' on the command line. Multiple extensions can be added with the '+ext filename' command line directive where the referenced file contains the classnames of the extensions with one classname per line.

# Sample Program
This is small test program that moves a red block back and forth like the Cylons from the 1980's Battlestar Galactica.

```
prog:	cylon

declare:
	X_POS	X1
	X_DIR	X2
	X_MAX	X3
	COLOR	X4
	POS		X5
	BASE	X6
	COUNT	X7
	SPEED	X8

code:
START:
	set X_POS=0
	set X_DIR=1
	set X_MAX=31
	set BASE=BITMAP:
	lui BASE, 4096
	addi BASE, BASE, -1024
	set COLOR=200
	set POS=BASE
	set SPEED=1000
	
MOVE:
	set POS=BASE
	set POS=POS+X_POS
	sb COLOR,POS[0]	
	
	set X_POS=X_POS+X_DIR
	
CHECK_X_OVERFLOW:
	jump CHECK_X_UNDERFLOW if X_POS < X_MAX
	set X_DIR=-1
	goto DELAY
	
CHECK_X_UNDERFLOW:
	jump DELAY if X_POS >= zero
	set X_DIR=1
	
DELAY:
	set COUNT=0
DELAY_LOOP:
	set COUNT=COUNT+1
	jump DELAY_LOOP if COUNT < SPEED
	
ERASE:
	sb X0,POS[0]
	goto MOVE
```

# Sample Binary
```
:1000000093000000130110009301f001130300c0de
:1000100037130000130303c01302f00fb302600094
:100020001304803eb3026000b382120023804200ba
:10003000b380200063c630001301f0ff6f00c000e2
:1000400063d4000013011000930300009383130096
:10005000e3ce83fe238002006ff0dffc000000008f
:00000001FF
```
# License
Licensed under [Creative Commons Non-Commercial Attribution 3.0](https://creativecommons.org/licenses/by-nc/3.0/legalcode).  For the avoidance of doubt, use as part of course materials or requirements at an educational institution that charges tuition is a commercial use and requires a commercial license.  Use by students for personal education is not a commercial use.  

# Warranty
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
