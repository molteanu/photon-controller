/*
 * Copyright 2015 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.vmware.photon.controller.api.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * The state transitions are:
 * <p/>
 * +----------------------------------+
 * |          PENDING_DELETE -------+ |
 * |           |    ^               | |
 * |           |    |               V V
 * CREATING ---+-> READY -------> DELETED
 * |           |   |                ^
 * |           |   |                |
 * |           V   V                |
 * +---------> ERROR ---------------+
 * <p/>
 * Note: see PRECONDITION_STATES for formalization of the above diagram
 * <p/>
 * - CREATING - the DB entities have been created, the flavor has an ID.
 * On success will transition to READY.
 * <p/>
 * - READY - means the flavor is created successfully.
 * <p/>
 * - PENDING_DELETE - means the flavor is was deleted by the user but there
 * are active VMs that were instantiated from the flavor.
 * <p/>
 * - DELETED - the flavor is a deleted tombstone
 * <p/>
 * - ERROR - flavor creation has failed
 */
public enum FlavorState {
  CREATING,
  READY,
  PENDING_DELETE,
  ERROR,
  DELETED;
  /**
   * The precondition states, eg. to get to the state indicated by the key, the object's current state
   * must be reflected in the set of valid precondition states for that key.
   */
  public static final Map<FlavorState, Set<FlavorState>> PRECONDITION_STATES =
      ImmutableMap.<FlavorState, Set<FlavorState>>builder()
          .put(CREATING, ImmutableSet.<FlavorState>of())
          .put(READY, Sets.immutableEnumSet(CREATING))
          .put(PENDING_DELETE, Sets.immutableEnumSet(READY))
          .put(ERROR, Sets.immutableEnumSet(CREATING, READY, PENDING_DELETE))
          .put(DELETED, Sets.immutableEnumSet(CREATING, READY, PENDING_DELETE, ERROR))
          .build();
}
