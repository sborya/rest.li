/*
   Copyright (c) 2020 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.d2.balancer.servers;

import java.util.List;
import java.util.stream.Collectors;

import com.linkedin.d2.discovery.stores.zk.ZooKeeperEphemeralStore;
import com.linkedin.d2.discovery.stores.zk.ZookeeperChildFilter;
import com.linkedin.d2.discovery.stores.zk.ZookeeperEphemeralPrefixGenerator;

import org.apache.commons.lang3.StringUtils;

/**
 * ChildPrefixFilter helps to filter the children in {@link ZooKeeperEphemeralStore}
 * to avoid reading other child data when not required. ChildPrefixFilter filter out other child names
 * that are not matching the same prefix generated by the given {@link ZookeeperEphemeralPrefixGenerator}
 * @author Nizar Mankulangara (nmankulangara@linkedin.com)
 */

public class ZookeeperPrefixChildFilter implements ZookeeperChildFilter
{
  private final ZookeeperEphemeralPrefixGenerator _prefixGenerator;

  public ZookeeperPrefixChildFilter(ZookeeperEphemeralPrefixGenerator prefixGenerator)
  {
    _prefixGenerator = prefixGenerator;
  }

  @Override
  public List<String> filter(List<String> children)
  {
    if (children == null)
    {
      return null;
    }

    return children.stream().filter(child -> {
      int separatorIndex = child.lastIndexOf('-');
      String childPrefix = separatorIndex > 0 ? child.substring(0, separatorIndex) : child;
      String ephemeralStorePrefix = _prefixGenerator.generatePrefix();
      return StringUtils.isEmpty(ephemeralStorePrefix) || childPrefix.equals(ephemeralStorePrefix) || childPrefix.equals(ZooKeeperEphemeralStore.DEFAULT_PREFIX);
    }).collect(Collectors.toList());
  }
}