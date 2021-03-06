/**
 * Copyright (c) 2002-2014 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.compiler.v2_2.planner.logical

import org.neo4j.cypher.internal.compiler.v2_2.InternalException
import org.neo4j.cypher.internal.compiler.v2_2.planner.logical.plans.LogicalPlan
import org.neo4j.cypher.internal.compiler.v2_2.planner.{QueryGraph, UnionQuery}

trait PlanningStrategy {
  def plan(plannerQuery: UnionQuery)(implicit context: LogicalPlanningContext, leafPlan: Option[LogicalPlan] = None): LogicalPlan
}

trait QueryGraphSolver {
  def emptyPlanTable: PlanTable
  def plan(queryGraph: QueryGraph)(implicit context: LogicalPlanningContext, leafPlan: Option[LogicalPlan] = None): LogicalPlan
}

trait TentativeQueryGraphSolver extends QueryGraphSolver {
  def tryPlan(queryGraph: QueryGraph)(implicit context: LogicalPlanningContext, leafPlan: Option[LogicalPlan] = None): Option[LogicalPlan]
  def plan(queryGraph: QueryGraph)(implicit context: LogicalPlanningContext, leafPlan: Option[LogicalPlan] = None): LogicalPlan =
    tryPlan(queryGraph).getOrElse(throw new InternalException("Failed to create a plan for the given QueryGraph " + queryGraph))
}
