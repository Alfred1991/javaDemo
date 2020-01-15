package org.xiaofengcanyue.generic;

/**
 * 使用泛型处理并行继承层次结构(parallel inheritance hierarchy)的问题。
 * 在该问题中包含两组相互对应的类，其中一组类中两个类之间的继承关系也意味着另外一组类中的对应类之间也存在继承关系。
 */
public class ParallelInheritanceHierarchy {
    abstract class Component{}
    abstract class ComponentRenderer <C extends Component>{
        abstract void render(C component);
    }
    class Button extends Component{}
    class ButtonRenderer extends ComponentRenderer<Button>{
        @Override
        void render(Button button) {
            //界面渲染
        }
    }
    public class GenericComponent{
        public void render(){
            ButtonRenderer renderer = new ButtonRenderer();
            renderer.render(new Button());
        }
    }
}
