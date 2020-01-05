package org.xiaofengcanyue.serialize;

import org.xiaofengcanyue.lifecycle.AboutLifecycle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Serializable仅是一个标记接口，并不包含任何需要实现的具体方法。
 * 实现该接口的目的是声明该java类的对象是可以被序列化的。
 * 对于支持序列化的java类的对象，可以使用java.io.ObjectOutputStream类和java.io.ObjectInputStream类的对象来完成java对象与字节流之间的相互转换。
 *
 * 在写入和读取时，虽然提供的参数或得到的返回值是单个java对象，但实际上操纵的是一个对象图。
 * 该对象图包括当前对象引用的其他对象，以及这些对象引用的另外的对象。
 * java的序列化机制会自动遍历整个对象图依次进行处理。
 * 在写入过程中，如果传递给ObjectOutputStream类的对象的writeObject方法的java对象并没有实现Serializable接口，那么writeObject方法会抛出java.io.NotSerializableException异常。
 * 而对于java对象中的域所引用的其他对象，如果这些对象本身没有实现Serializable接口，那么这个域不会出现在序列化之后的字节流中。
 *
 * 在默认的序列化实现中，java对象中的非静态（static）和非瞬时（transient）域都会自动被包括进来。
 * 添加一个serialPersistentFields域可以声明序列化时要包含的域，例如：
 *  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("password",String.class)}
 */
public class AboutSerialize {

    public static class User implements Serializable {
        private String name;
        private String email;
        public User(String name,String email){
            this.name=name;
            this.email=email;
        }
        public User(AboutLifecycle.User user){
            this.name=user.getName();
            this.email=user.getEmail();
        }
        public String getName(){
            return this.name;
        }

        public String getEmail() {
            return email;
        }
    }

    public static class WriteUser{
        Path path = Paths.get("user.bin");

        public void write(User user) throws IOException {

            try(ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(path))){
                output.writeObject(user);
            }
        }

        public static void main(String[] args) throws IOException {
            WriteUser writeUser = new WriteUser();
            User user = new User("Alex","alex@example.com");
            writeUser.write(user);
        }
    }

    public static class ReadUser{
        public User readUser() throws IOException,ClassNotFoundException{
            Path path = Paths.get("user.bin");
            try(ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))){
                User user = (User) input.readObject();
                return user;
            }
        }

        public static void main(String[] args) throws IOException, ClassNotFoundException {
            ReadUser readUser = new ReadUser();
            User user = readUser.readUser();
            System.out.printf("name:%s,email:%s",user.getName(),user.getEmail());
        }
    }
    /**
     * 自定义的序列化逻辑由writeObject和readObject方法实现。
     *
     * 反序列化过程没有调用构造方法，因此需要确保readObject中经过了完整的初始化。
     */
    public static class NewUser implements Serializable{
        /**
         * 序列化的版本兼容性判断由java类中的全局唯一标识符serialVersionUID来实现。
         * 当java类实现了Serializable接口时，需要声明该java类唯一的序列化版本号。这个版本号会被包括在序列化后的字节流中。
         * 在进行读取时会比较从字节流中得到的版本号域对应java类中声明的版本号是否一致，以确定两者是否兼容。
         * 如果java类实现了Serializable接口，但没有显式地声明serialVersionUID域，那么虚拟机会根据java类的各种元素的特征计算出一个散列值，作为serialVersionUID的默认值。
         */
        private static final long serialVersionUID = 1L;
        private transient int age;
        public NewUser(int age){
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        private void writeObject(ObjectOutputStream output) throws IOException{
            output.defaultWriteObject();//默认的序列化，写入非静态非瞬时的域
            output.writeInt(getAge());
        }

        private void readObject(ObjectInputStream input) throws IOException,ClassNotFoundException{
            input.defaultReadObject();//默认的反序列化，读取非静态非瞬时的域
            int age = input.readInt();
            this.age = age;
        }
    }

    public static class NewUser2 implements Serializable{
        private static final long serialVersionUID = 1L;
        private transient Date birthDate;
        public NewUser2(Date birthDate){
            this.birthDate = birthDate;
        }
        public int getAge(){
            return dateToAge(birthDate);
        }

        private Date ageToDate(int age){
            return new Date();
        }

        private int dateToAge(Date date){
            return 0;
        }


        private void writeObject(ObjectOutputStream output) throws IOException{
            output.defaultWriteObject();//默认的序列化，写入非静态非瞬时的域
            int age = dateToAge(birthDate);
            output.writeInt(age);
        }

        private void readObject(ObjectInputStream input) throws IOException,ClassNotFoundException{
            input.defaultReadObject();//默认的反序列化，读取非静态非瞬时的域
            int age = input.readInt();
            this.birthDate = ageToDate(age);

            input.registerValidation(new UserValidator(this),0);//注册反序列化验证器
        }

        /**
         * 使用ObjectInputValidation的实现类来进行反序列化安全性验证
         */
        private static class UserValidator implements ObjectInputValidation{
            private NewUser2 user;

            public UserValidator(NewUser2 user){
                this.user = user;
            }

            @Override
            public void validateObject() throws InvalidObjectException {
                if(user.getAge() < 0){
                    throw new InvalidObjectException("非法的年龄数值");
                }
            }
        }
    }

    /**
     * 序列化对象替换的实现方法是writeReplace和readResolve。
     * 它们在序列化之前把当前对象替换为另一个对象，在反序列化之后再把该对象还原为原对象。
     * 其中writeReplace在序列化之前被调用，readResolve在反序列化之后被调用。
     */
    public static class Order implements Serializable{
        private User user;
        private String id;
        public Order(String id,User user){
            this.id=id;
            this.user=user;
        }

        public String getId() {
            return id;
        }

        private Object writeReplace() throws ObjectStreamException{
            return new OrderTo(this);
        }
    }

    public static class OrderTo implements Serializable{
        private String orderId;
        public OrderTo(Order order){
            this.orderId = order.getId();
        }
        private Object readResolve() throws ObjectStreamException{
            return null;
        }
    }

    /**
     * 如果需要对序列化过程进行完整地控制，可以实现Externalizable接口，Externalizable接口继承自Serializable接口。
     *
     * 实现Externalizable接口的java类必须具备一个不带参数的公开构造方法。在反序列化过程中会先调用该构造方法得到一个新的对象，再在此对象上调用readExternal方法。
     */
    public static class ExternalizableUser implements Externalizable{
        private String name;
        private String email;
        public ExternalizableUser(){}
        public ExternalizableUser(String name,String email){
            this.name=name;
            this.email=email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(getName());
            out.writeUTF(getEmail());
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            name = in.readUTF();
            email = in.readUTF();
        }
    }
}
