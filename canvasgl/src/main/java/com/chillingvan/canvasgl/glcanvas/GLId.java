/*
 *
 *  *
 *  *  * Copyright (C) 2016 ChillingVan
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.chillingvan.canvasgl.glcanvas;

// This mimics corresponding GL functions.
public interface GLId {
    public int generateTexture();

    public void glGenBuffers(int n, int[] buffers, int offset);

    public void glDeleteTextures(int n, int[] textures, int offset);

    public void glDeleteBuffers(int n, int[] buffers, int offset);

    public void glDeleteFramebuffers(int n, int[] buffers, int offset);
}
