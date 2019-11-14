package com.cnsugar.common.cache.ehcache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * 
 * @description
 * @author star 2010-9-7 下午12:11:03
 **/
public class CacheHelper extends UCacheManager {
	protected final static Logger log = LoggerFactory.getLogger(CacheHelper.class);
	protected static Map<String, CacheHelper> helpers = new HashMap<String, CacheHelper>();
	protected Cache cache;
	protected volatile long lastFlushTime = 0;

	private CacheHelper(String cacheName) {
		super();
		try {
			cache = manager.getCache(cacheName);
		} catch (Exception e) {
			log.error("Get cache(" + cacheName + ") instance fail: " + e.getMessage(), e);
		}
	}

	public static CacheHelper getCache(String cacheName) {
		CacheHelper ch = helpers.get(cacheName);
		if (ch == null) {
			ch = new CacheHelper(cacheName);
			helpers.put(cacheName, ch);
		}
		return ch;
	}

	/**
	 * 从缓存中获取对象
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Object key) {
		Object s = null;
		if (cache != null) {
			try {
				Element elem = cache.get(key);
				if (elem != null) {
					s = elem.getObjectValue();
				}
			} catch (Exception e) {
				log.error("Get obj[" + key + "] from cache(" + cache.getName() + ") fail: " + e.getMessage(), e);
			}
		}
		return (T)s;
	}

	/**
	 * 把对象放入缓存中
	 * 
	 * @param key
	 * @param value
	 */
	public boolean put(Object key, Object value) {
		if (cache != null) {
			try {
				cache.put(new Element(key, value));
				//隔10秒以上才刷新一次
				if (cache.getCacheConfiguration().isDiskPersistent() && System.currentTimeMillis() - lastFlushTime >= 10000) {
					lastFlushTime = System.currentTimeMillis();
					cache.flush();
				}
				return true;
			} catch (Exception e) {
				log.error("Put obj[" + key + "=" + value + "] to cache(" + cache.getName() + ") fail: " + e.getMessage(), e);
			}
		}
		return false;
	}

	/**
	 * 获取缓存
	 *
	 * @return
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * 获取缓冲中的信息
	 * 
	 * @param key
	 * @return
	 */
	public Element getElement(Object key) {
		if (cache != null) {
			try {
				return cache.get(key);
			} catch (Exception e) {
				log.error("Get element[" + key + "] from cache(" + cache.getName() + ") fail: " + e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * 返回cache大小
	 * 
	 * @return
	 */
	public int size() {
		if (cache != null) {
			try {
				return cache.getSize();
			} catch (Exception e) {
				log.error("Get cache(" + cache.getName() + ") size fail: " + e.getMessage(), e);
			}
		}
		return 0;
	}

	public List getKeys() {
		if (cache != null) {
			try {
				return cache.getKeys();
			} catch (Exception e) {
				log.error("Get cache(" + cache.getName() + ") keys fail: " + e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * 把对象从缓存中移出
	 * 
	 * @param key
	 */
	public boolean remove(Object key) {
		if (cache != null) {
			try {
				if (cache.remove(key)) {
					if (cache.getCacheConfiguration().isEternal()) {
						cache.flush();
					}
					return true;
				}
				return false;
			} catch (Exception e) {
				log.error("Remove obj[" + key + "] from cache(" + cache.getName() + ") failed.", e);
			}
		}
		return false;
	}

	/**
	 * 检查缓存中中否存在
	 * 
	 * @param key
	 */
	public boolean exists(Object key) {
		if (cache != null) {
			try {
				if (get(key) != null) {
					return true;
				}
			} catch (Exception e) {
				log.error("Check obj[" + key + "] is exists in cache(" + cache.getName() + ") failed.", e);
			}
		}
		return false;
	}

	public String getString(Object key) {
		return (String) get(key);
	}

	public Map getMap(Object key) {
		return get(key);
	}

	public <T> List<T> getList(Object key) {
		return get(key);
	}

	public int getInt(Object key) {
		Object o = get(key);
		if (o == null) {
			return -1;
		}
		return (int) o;
	}

	public short getShort(Object key) {
		Object o = get(key);
		if (o == null) {
			return -1;
		}
		return (short) o;
	}

	public long getLong(Object key) {
		Object o = get(key);
		if (o == null) {
			return -1;
		}
		return (long) o;
	}

	public float getFloat(Object key) {
		Object o = get(key);
		if (o == null) {
			return -1;
		}
		return (float) o;
	}

	public double getDouble(Object key) {
		Object o = get(key);
		if (o == null) {
			return -1;
		}
		return (double) o;
	}

	public byte[] getBytes(Object key) {
		return (byte[]) get(key);
	}

	public Byte getByte(Object key) {
		return (Byte) get(key);
	}

	public int[] getIntArray(Object key) {
		return (int[]) get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] getArray(Object key, Class<T> clazz) {
		return (T[]) get(key);
	}
}
