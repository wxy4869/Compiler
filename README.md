# Compiler

> 三秋 编译 实验



## 文法定义

```c
CompUnit		→ {Decl} {FuncDef} MainFuncDef
Decl			→ ConstDecl | VarDecl
ConstDecl		→ 'const' BType ConstDef { ',' ConstDef } ';'
BType			→ 'int'
ConstDef		→ Ident { '[' ConstExp ']' } '=' ConstInitVal
ConstInitVal		→ ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
VarDecl			→ BType VarDef { ',' VarDef } ';' 
VarDef			→ Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
InitVal			→ Exp | '{' [ InitVal { ',' InitVal } ] '}'
FuncDef			→ FuncType Ident '(' [FuncFParams] ')' Block
MainFuncDef		→ 'int' 'main' '(' ')' Block
FuncType		→ 'void' | 'int'
FuncFParams		→ FuncFParam { ',' FuncFParam }
FuncFParam		→ BType Ident ['[' ']' { '[' ConstExp ']' }]
Block			→ '{' { BlockItem } '}'
BlockItem		→ Decl | Stmt
Stmt			→ LVal '=' Exp ';'
			| Block
			| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
			| 'break' ';' 
			| 'continue' ';'
			| 'return' [Exp] ';' 
			| LVal '=' 'getint''('')'';'
			| 'printf''('FormatString{','Exp}')'';'
Exp			→ AddExp
Cond			→ LOrExp 
LVal			→ Ident {'[' Exp ']'}
PrimaryExp		→ '(' Exp ')' | LVal | Number
Number			→ IntConst
UnaryExp		→ PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
UnaryOp			→ '+' | '−' | '!'
FuncRParams		→ Exp { ',' Exp }
MulExp			→ UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
AddExp			→ MulExp | AddExp ('+' | '−') MulExp
RelExp			→ AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
EqExp			→ RelExp | EqExp ('==' | '!=') RelExp
LAndExp			→ EqExp | LAndExp '&&' EqExp
LOrExp			→ LAndExp | LOrExp '||' LAndExp
ConstExp		→ AddExp
```



## 运行方式

- 新建一个项目，将 src 文件夹复制到新项目中。

- 新建 testfile.txt 文件，将要编译的源代码写到该文件中。

- 程序入口在 `src/Compiler.java`，如需修改输出阶段，可修改该文件中的 `op`。

- 文件组织结构：

  ```shell
  .
  ├── src
  ├── testfile.txt	# 要编译的源代码 
  ├── output.txt		# 词法分析和语法分析的输出
  ├── llvm_ir.txt		# llvm 中间代码的输出
  └── error.txt		# 错误处理的输出
  ```



## 参考

- <a href="https://buaa-se-compiling.github.io/miniSysY-tutorial/">GitHub：miniSysY-tutorial</a>
- <a href="https://releases.llvm.org/6.0.0/docs/LangRef.html">LLVM 6.0 文档</a>
