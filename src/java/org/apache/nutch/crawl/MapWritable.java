/**
 * Copyright 2007 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.crawl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.io.*;

/**
 * A Writable Map.
 */
public class MapWritable extends AbstractMapWritable
  implements Map<Writable, Writable> {

  private Map<Writable, Writable> instance;
  
  /** Default constructor. */
  public MapWritable() {
    super();
    this.instance = new HashMap<Writable, Writable>();
  }
  
  /**
   * Copy constructor.
   * 
   * @param other the map to copy from
   */
  public MapWritable(MapWritable other) {
    this();
    copy(other);
  }
  
  /** {@inheritDoc} */
  public void clear() {
    instance.clear();
  }

  /** {@inheritDoc} */
  public boolean containsKey(Object key) {
    return instance.containsKey(key);
  }

  /** {@inheritDoc} */
  public boolean containsValue(Object value) {
    return instance.containsValue(value);
  }

  /** {@inheritDoc} */
  public Set<Map.Entry<Writable, Writable>> entrySet() {
    return instance.entrySet();
  }

  /** {@inheritDoc} */
  public Writable get(Object key) {
    return instance.get(key);
  }
  
  /** {@inheritDoc} */
  public boolean isEmpty() {
    return instance.isEmpty();
  }

  /** {@inheritDoc} */
  public Set<Writable> keySet() {
    return instance.keySet();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public Writable put(Writable key, Writable value) {
    addToMap(key.getClass());
    addToMap(value.getClass());
    return instance.put(key, value);
  }

  /** {@inheritDoc} */
  public void putAll(Map<? extends Writable, ? extends Writable> t) {
    for (Map.Entry<? extends Writable, ? extends Writable> e: t.entrySet()) {
      instance.put(e.getKey(), e.getValue());
    }
  }

  /** {@inheritDoc} */
  public Writable remove(Object key) {
    return instance.remove(key);
  }

  /** {@inheritDoc} */
  public int size() {
    return instance.size();
  }

  /** {@inheritDoc} */
  public Collection<Writable> values() {
    return instance.values();
  }
  
  // Writable
  
  /** {@inheritDoc} */
  @Override
  public void write(DataOutput out) throws IOException {
    // Write out the number of entries in the map
    
    out.writeInt(instance.size());

    super.write(out);
    
    // Then write out each key/value pair
    
    for (Map.Entry<Writable, Writable> e: instance.entrySet()) {
      // NUTCH-676 changed order
      out.writeByte(getId(e.getKey().getClass()));
      out.writeByte(getId(e.getValue().getClass()));
      e.getKey().write(out);
      e.getValue().write(out);
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public void readFields(DataInput in) throws IOException {
    // Read the number of entries in the map    
    int entries = in.readInt();
    // NUTCH-676 - this is changed order from hadoop

    super.readFields(in);
    
    // First clear the map.  Otherwise we will just accumulate
    // entries every time this method is called.
    this.instance.clear();
    
    
    // Then read each key/value pair
    
    for (int i = 0; i < entries; i++) {
      Writable key = (Writable) ReflectionUtils.newInstance(getClass(
          in.readByte()), getConf());
      
      Writable value = (Writable) ReflectionUtils.newInstance(getClass(
          in.readByte()), getConf());

      // NUTCH-676 changed order of data around
      
      key.readFields(in);
      value.readFields(in);
      instance.put(key, value);
    }
  }

  public boolean equals(Object other) {
    if (other == null ||
        !(other instanceof MapWritable))
      return false;
    MapWritable otherW = (MapWritable)other;
    return this.instance.equals(otherW.instance);
  }

  public int hashCode() {
    return this.instance.hashCode();
  }
}
