package org.xiaofengcanyue.script;

import javax.script.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AboutScripts {


    public static void main(String[] args) throws Exception{

        Scanner scanner = new Scanner(System.in).useDelimiter("\n");



        CompiledScript script = compile("print('Hello World!');");
        if(script == null){
            return;
        }
        for(int i=0; i < 100;i++){
            script.eval();
        }

    }

    public void useCustomBinding() throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        Bindings bindings = new SimpleBindings();
        bindings.put("hobby","playing games");
        engine.eval("print('I like '+hobby);",bindings);
    }

    public void scriptToFile() throws IOException,ScriptException{
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        ScriptContext context = engine.getContext();
        context.setWriter(new FileWriter("output.txt"));
        engine.eval("print('Hello World!');");
    }

    public void scriptContextAttribute(){

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        ScriptContext context = engine.getContext();
        context.setAttribute("name","Alex",ScriptContext.GLOBAL_SCOPE);
        context.setAttribute("name","Blob",ScriptContext.ENGINE_SCOPE);
        context.getAttribute("name");

    }

    public void scriptContextBindings() throws ScriptException{
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        ScriptContext context = engine.getContext();
        Bindings bindings1 = engine.createBindings();
        bindings1.put("name","Alex");
        context.setBindings(bindings1,ScriptContext.GLOBAL_SCOPE);
        Bindings bindings2 = engine.createBindings();
        bindings2.put("name","Bob");
        context.setBindings(bindings2,ScriptContext.ENGINE_SCOPE);
        engine.eval("print(name);");
    }

    public void useScriptContextValues() throws ScriptException{
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        ScriptContext context = engine.getContext();
        Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("name","Alex");
        engine.eval("print(name);");
    }

    public void attributeInBindings() throws ScriptException{
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        ScriptContext context = engine.getContext();
        context.setAttribute("name","Alex",ScriptContext.ENGINE_SCOPE);
        engine.eval("print(name)");
    }

    public static CompiledScript compile(String scriptText) throws ScriptException{
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        if(engine instanceof Compilable){
            CompiledScript script = ((Compilable)engine).compile(scriptText);
            return script;
        }
        return null;
    }
}
