#!/usr/bin/perl

my $n = 340;
# put new lines in one-line datasets files 
# remove all data from each serie but the $n first values
while(<>) {
    chomp;
    s/([^:]+:[^:]+,)/$1\n/g;
    s/(\:\[)((.+?,){$n})([^\],]+\])/$1$3]/g;
    s/,+\]/\]/g;
    print "$_\n";
}

