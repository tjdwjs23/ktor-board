<div display="flex" class="css-18uwpz8"><div aria-expanded="false" aria-haspopup="dialog" style="position: fixed; user-select: none; pointer-events: none;"></div><div id="chat-room-message-3" class="css-0"><div class="css-1j17jy3"><p color="$color_text_primary" class="css-np9kzu">Sure! Here’s the README in English:</p>
<hr>
<h1 color="$color_text_primary" font-size="30px" font-weight="600" class="css-xlls5m">Ktor-Based Application</h1>
<p color="$color_text_primary" class="css-np9kzu">This project is a server application built using the Ktor framework. It is developed in Kotlin and utilizes various libraries for database connectivity and JSON serialization.</p>
<h2 color="$color_text_primary" font-size="24px" font-weight="600" class="css-13o5nww">Tech Stack</h2>
<ul color="$color_text_primary" class="css-25cm1">
<li color="$color_text_primary" class="css-10efh88"><strong color="$color_text_primary" class="css-q6mb20">Kotlin</strong>: 2.1.20</li>
<li color="$color_text_primary" class="css-10efh88"><strong color="$color_text_primary" class="css-q6mb20">Ktor</strong>: 3.1.1</li>
<li color="$color_text_primary" class="css-10efh88"><strong color="$color_text_primary" class="css-q6mb20">Logback</strong>: (version to be specified)</li>
<li color="$color_text_primary" class="css-10efh88"><strong color="$color_text_primary" class="css-q6mb20">Ktorm</strong>: 4.1.1</li>
<li color="$color_text_primary" class="css-10efh88"><strong color="$color_text_primary" class="css-q6mb20">HikariCP</strong>: 6.2.1</li>
<li color="$color_text_primary" class="css-10efh88"><strong color="$color_text_primary" class="css-q6mb20">MySQL</strong>: 8.0.33</li>
</ul>
<h2 color="$color_text_primary" font-size="24px" font-weight="600" class="css-13o5nww">Features</h2>
<ul color="$color_text_primary" class="css-25cm1">
<li color="$color_text_primary" class="css-10efh88">Ktor server setup</li>
<li color="$color_text_primary" class="css-10efh88">JSON serialization and content negotiation</li>
<li color="$color_text_primary" class="css-10efh88">YAML configuration file support</li>
<li color="$color_text_primary" class="css-10efh88">MySQL database connectivity</li>
<li color="$color_text_primary" class="css-10efh88">Connection pooling with HikariCP</li>
</ul>
<h2 color="$color_text_primary" font-size="24px" font-weight="600" class="css-13o5nww">Installation and Running</h2>
<ol color="$color_text_primary" class="css-1f7gyt9">
<li color="$color_text_primary" class="css-10efh88">
<p color="$color_text_primary" class="css-np9kzu"><strong color="$color_text_primary" class="css-q6mb20">Environment Setup</strong>: Ensure that Kotlin and Gradle are installed.</p>
</li>
<li color="$color_text_primary" class="css-10efh88">
<p color="$color_text_primary" class="css-np9kzu"><strong color="$color_text_primary" class="css-q6mb20">Install Dependencies</strong>: Run the following command in the project directory to download dependencies.</p>
<pre><div width="100%" overflow="hidden" class="css-p4nim3"><div display="flex" width="100%" class="css-1jfinf2"><p color="$color_surface_white" class="css-17cksfn">bash</p><button class="css-0"><svg width="20" height="20" viewBox="0 0 25 25" fill="currentColor" xmlns="http://www.w3.org/2000/svg" color="#ffffffff"><path d="M10.5 18.437C9.95 18.437 9.47917 18.2412 9.0875 17.8495C8.69583 17.4579 8.5 16.987 8.5 16.437V4.43702C8.5 3.88702 8.69583 3.41619 9.0875 3.02452C9.47917 2.63285 9.95 2.43702 10.5 2.43702H18.5C19.05 2.43702 19.5208 2.63285 19.9125 3.02452C20.3042 3.41619 20.5 3.88702 20.5 4.43702V16.437C20.5 16.987 20.3042 17.4579 19.9125 17.8495C19.5208 18.2412 19.05 18.437 18.5 18.437H10.5ZM10.5 16.437H18.5V4.43702H10.5V16.437ZM6.5 22.437C5.95 22.437 5.47917 22.2412 5.0875 21.8495C4.69583 21.4579 4.5 20.987 4.5 20.437V6.43702H6.5V20.437H16.5V22.437H6.5Z" fill="currentColor"></path></svg></button></div><pre highlighter="hljs" style="display: block; overflow-x: auto; padding: 10px 16px; background: rgb(30, 30, 30); color: rgb(220, 220, 220); white-space: pre-wrap;"><code class="language-bash" style="white-space: pre;"><span>./gradlew build
</span></code></pre></div></pre>
</li>
<li color="$color_text_primary" class="css-10efh88">
<p color="$color_text_primary" class="css-np9kzu"><strong color="$color_text_primary" class="css-q6mb20">Run the Application</strong>: Execute the following command to run the application.</p>
<pre><div width="100%" overflow="hidden" class="css-p4nim3"><div display="flex" width="100%" class="css-1jfinf2"><p color="$color_surface_white" class="css-17cksfn">bash</p><button class="css-0"><svg width="20" height="20" viewBox="0 0 25 25" fill="currentColor" xmlns="http://www.w3.org/2000/svg" color="#ffffffff"><path d="M10.5 18.437C9.95 18.437 9.47917 18.2412 9.0875 17.8495C8.69583 17.4579 8.5 16.987 8.5 16.437V4.43702C8.5 3.88702 8.69583 3.41619 9.0875 3.02452C9.47917 2.63285 9.95 2.43702 10.5 2.43702H18.5C19.05 2.43702 19.5208 2.63285 19.9125 3.02452C20.3042 3.41619 20.5 3.88702 20.5 4.43702V16.437C20.5 16.987 20.3042 17.4579 19.9125 17.8495C19.5208 18.2412 19.05 18.437 18.5 18.437H10.5ZM10.5 16.437H18.5V4.43702H10.5V16.437ZM6.5 22.437C5.95 22.437 5.47917 22.2412 5.0875 21.8495C4.69583 21.4579 4.5 20.987 4.5 20.437V6.43702H6.5V20.437H16.5V22.437H6.5Z" fill="currentColor"></path></svg></button></div><pre highlighter="hljs" style="display: block; overflow-x: auto; padding: 10px 16px; background: rgb(30, 30, 30); color: rgb(220, 220, 220); white-space: pre-wrap;"><code class="language-bash" style="white-space: pre;"><span>./gradlew run
</span></code></pre></div></pre>
</li>
</ol>
<h2 color="$color_text_primary" font-size="24px" font-weight="600" class="css-13o5nww">Development Mode</h2>
<p color="$color_text_primary" class="css-np9kzu">To run in development mode, you can set JVM arguments by adding the <code node="[object Object]" style="white-space: break-spaces;">development</code> property.</p>
<pre><div width="100%" overflow="hidden" class="css-p4nim3"><div display="flex" width="100%" class="css-1jfinf2"><p color="$color_surface_white" class="css-17cksfn">bash</p><button class="css-0"><svg width="20" height="20" viewBox="0 0 25 25" fill="currentColor" xmlns="http://www.w3.org/2000/svg" color="#ffffffff"><path d="M10.5 18.437C9.95 18.437 9.47917 18.2412 9.0875 17.8495C8.69583 17.4579 8.5 16.987 8.5 16.437V4.43702C8.5 3.88702 8.69583 3.41619 9.0875 3.02452C9.47917 2.63285 9.95 2.43702 10.5 2.43702H18.5C19.05 2.43702 19.5208 2.63285 19.9125 3.02452C20.3042 3.41619 20.5 3.88702 20.5 4.43702V16.437C20.5 16.987 20.3042 17.4579 19.9125 17.8495C19.5208 18.2412 19.05 18.437 18.5 18.437H10.5ZM10.5 16.437H18.5V4.43702H10.5V16.437ZM6.5 22.437C5.95 22.437 5.47917 22.2412 5.0875 21.8495C4.69583 21.4579 4.5 20.987 4.5 20.437V6.43702H6.5V20.437H16.5V22.437H6.5Z" fill="currentColor"></path></svg></button></div><pre highlighter="hljs" style="display: block; overflow-x: auto; padding: 10px 16px; background: rgb(30, 30, 30); color: rgb(220, 220, 220); white-space: pre-wrap;"><code class="language-bash" style="white-space: pre;"><span>./gradlew run -Ddevelopment=</span><span style="color: rgb(86, 156, 214);">true</span><span>
</span></code></pre></div></pre>
<h2 color="$color_text_primary" font-size="24px" font-weight="600" class="css-13o5nww">License</h2>
<p color="$color_text_primary" class="css-np9kzu">This project is licensed under the MIT License. Please refer to the LICENSE file for more details.</p>
<hr>
<p color="$color_text_primary" class="css-np9kzu">Feel free to modify any sections as needed!</p></div></div></div>