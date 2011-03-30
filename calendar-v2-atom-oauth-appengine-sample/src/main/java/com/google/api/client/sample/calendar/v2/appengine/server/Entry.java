/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.sample.calendar.v2.appengine.server;

import com.google.api.client.googleapis.xml.atom.AtomPatchRelativeToOriginalContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DataUtil;
import com.google.api.client.util.Key;
import com.google.api.client.xml.atom.AtomContent;

import java.io.IOException;
import java.util.List;

/**
 * Generic Atom Entry.
 *
 * @author Yaniv Inbar
 */
public class Entry implements Cloneable {

  @Key
  String summary;

  @Key
  String title;

  @Key
  String updated;

  @Key("link")
  List<Link> links;


  @Override
  protected Entry clone() {
    return DataUtil.clone(this);
  }

  void executeDelete(HttpTransport transport) throws IOException {
    HttpRequest request = transport.buildDeleteRequest();
    request.setUrl(getEditLink());
    Auth.execute(request).ignore();
  }

  Entry executeInsert(HttpTransport transport, CalendarUrl url) throws IOException {
    HttpRequest request = transport.buildPostRequest();
    request.url = url;
    AtomContent content = new AtomContent();
    content.namespaceDictionary = Namespace.DICTIONARY;
    content.entry = this;
    request.content = content;
    return Auth.execute(request).parseAs(getClass());
  }

  Entry executePatchRelativeToOriginal(HttpTransport transport, Entry original) throws IOException {
    HttpRequest request = transport.buildPatchRequest();
    request.setUrl(getEditLink());
    AtomPatchRelativeToOriginalContent content = new AtomPatchRelativeToOriginalContent();
    content.namespaceDictionary = Namespace.DICTIONARY;
    content.originalEntry = original;
    content.patchedEntry = this;
    request.content = content;
    return Auth.execute(request).parseAs(getClass());
  }

  String getEditLink() {
    return Link.find(links, "edit");
  }
}