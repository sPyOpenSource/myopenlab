/*
 * Copyright (C) 2022 spy
 *
 * This program is free software: you can redistribute it and/or modify
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
package VisualLogic;

import java.io.Serializable;

/**
 *
 * @author spy
 */
public class PinsSettingsItem implements Serializable
{
    public String name;
    public String dt; // zb: C_INTEGER
    public int intDT; // zb: 0,1,2,3 aldi DT!
    public String io="";
}
