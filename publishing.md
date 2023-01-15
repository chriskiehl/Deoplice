# Publishing to Maven 

Pain in the bum. 


### Pre-publish testing

1. Run the `publishMavenJavaPublicationToMavenLocal` task to write the jar to `~/.m2/repository`
2. In the DeopliceInstall repo, add a dep on the local `.m2/` repository 
3. verify that the annotation processor runs as expected 


### Releasing: 

Follow the docs here: https://central.sonatype.org/publish/publish-gradle

TL;DR: 

1. Run the `publish` task 
2. Go to `https://s01.oss.sonatype.org` and log in 
3. Click `Staging Repositories` under `Build Promotion` 
4. Select the package. Click `Close`
5. Wait. If all goes well, it will pass the "requirements" and be ready for release 
6. Click the `Release` button 
7. Wait a few hours for sync to maven central...? 


### Signing:
For whatever reason (bug? user error? platform quirk?), the signing plugin sets the keyring location incorrectly when reading from gradle.properties. I gave up on figuring out why. Instead, it's read from a local file (I *also* had trouble getting environment variables to work and gave up). 

#### Location: 

Files are stored in the project root: 

```
/deoplice
  - signingkey
```

#### Signing Key: 

The key must be in the "armored ascii" format: 

```
cd ~/Documents/deoplice
gpg --armor --export-secret-keys me@chriskiehl.com 
```

This dumps it to stdout. Copy this and paste it into a file called `signingkey`. 

It can be done via CLI like this:

```
gpg --armor --export-secret-keys me@chriskiehl.com > singingkey
```
But the encoding used when writing a file from a windows command prompt is wonky and gpg doesn't like it. I dunno how to configure it (and current you is too lazy to figure it out). 

#### Signing Pass:

The password is stored in gradle.properties as `signing.password`. 









