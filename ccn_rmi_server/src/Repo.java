import java.io.IOException;
import java.util.LinkedHashMap;  
import java.util.Map;
import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReentrantLock; 

public class Repo<K, V> extends LinkedHashMap<K, V> {
	
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;  
    private final Lock lock = new ReentrantLock();  
  
    public Repo()  
    {  
        super(Parameters.repo_capacity, DEFAULT_LOAD_FACTOR, true);  
    }  
   
  
    @Override  
    public V get(Object key)  
    {  
        try {  
            lock.lock();  
            return super.get(key);  
        }  
        finally {  
            lock.unlock();  
        }  
    }  
  
    @Override  
    public V put(K key, V value)  
    {  
        try {  
            lock.lock();  
            return super.put(key, value);  
        }  
        finally {  
            lock.unlock();  
        }  
    }  
    

}
