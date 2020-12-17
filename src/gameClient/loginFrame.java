//package gameClient;
//
//import java.awt.*;
//
//public class loginFrame implements Runnable {
//    @Override
//    public void run() {
//        try {
//            synchronized(this){
//                this.wait();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Frame win = new Frame("Pokemon game", Ex2.Arena);
//        win.setSize(700, 500);
//        win.setVisible(true);
//        while (true) {
//            win.repaint();
//            try {
//                Thread.sleep(1000/144); //refresh rate
////                synchronized(this){
////                    this.wait();
////                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//}