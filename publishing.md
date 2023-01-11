# Publishing to Maven 

Much pain was encountered while trying to publish to Maven. 

### Pre-publish testing

1. Run the `publishMavenJavaPublicationToMavenLocal` task to write the jar to `~/.m2/repository`
2. 



### Signing:
For whatever reason (bug? user error? platform quirk?), the signing plugin sets the keyring location incorrectly when reading from gradle.properties. I gave up on figuring out why. Instead, it's read from a local file (I *also* had trouble getting environment variables to work and gave up). 

#### Location: 

Files are stored in the project root: 

```
/deoplice
  - signingkey
  - signingpass
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

The password is vanilla text. It can be retrieved from the appropriate password manager.









